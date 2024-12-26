import pandas as pd
from flask import Flask, request, jsonify
import pickle
from sklearn.preprocessing import LabelEncoder

app = Flask(__name__)

# Załaduj wytrenowany model i encoder kategorii
with open('restaurant_recommender.pkl', 'rb') as model_file:
    model = pickle.load(model_file)

with open('le_food.pkl', 'rb') as encoder_file:
    le_food = pickle.load(encoder_file)

# Dane testowe dla `/test-data`
user_history = pd.DataFrame({
    "userEmail": ["user1@example.com"] * 3,
    "restaurantId": [101, 102, 103],
    "restaurantName": ["Pizza Place", "Sushi World", "Burger King"],
    "userReviewRating": [5, 4, 3],
    "orderCount": [10, 7, 5],
    "lastOrder": ["2024-01-01", "2023-12-25", "2024-01-10"],
    "preferredFoodType": ["Italian", "Sushi", "Fast Food"],
    "frequencyPerMonth": [2, 1, 1],
    "averageUserRating": [4.7] * 3
})

restaurant_attributes = pd.DataFrame({
    "restaurantId": [101, 102, 103],
    "restaurantName": ["Pizza Place", "Sushi World", "Burger King"],
    "foodType": ["Italian", "Sushi", "Fast Food"],
    "avgCost": [20, 35, 15],
    "avgRating": [4.5, 4.7, 4.2],
    "deliveryTimeMinutes": [30, 45, 25],
    "discountAvailable": [1, 0, 1],
    "popularityScore": [90, 85, 80]
})


@app.route('/test-data', methods=['GET'])
def test_data():
    """Endpoint zwracający dane testowe"""
    history_json = user_history.to_dict(orient='records')
    attributes_json = restaurant_attributes.to_dict(orient='records')
    response = {
        "userHistory": history_json,
        "restaurantAttributes": attributes_json
    }
    return jsonify(response)


@app.route('/recommend', methods=['POST'])
def recommend():
    """Endpoint rekomendacji"""
    data = request.json

    # Tworzenie DataFrame z danych użytkownika i restauracji
    user_history_df = pd.DataFrame(data["userHistory"])
    restaurant_attributes_df = pd.DataFrame(data["restaurantAttributes"])

    # Łączenie danych
    merged_data = pd.merge(user_history_df, restaurant_attributes_df, on="restaurantId")

    # Kodowanie zmiennych kategorycznych za pomocą załadowanego encoder'a
    merged_data["foodType"] = le_food.transform(merged_data["foodType"])

    # Predykcja za pomocą załadowanego modelu
    predictions = model.predict(merged_data.drop(columns=["userReviewRating"]))
    merged_data["predictedRating"] = predictions

    # Sortowanie według przewidywanej oceny
    recommended_restaurants = merged_data.sort_values(by="predictedRating", ascending=False)

    # Zwracanie wyników w formacie JSON
    return jsonify(recommended_restaurants[["restaurantName", "predictedRating"]].to_dict(orient="records"))


if __name__ == '__main__':
    app.run()
