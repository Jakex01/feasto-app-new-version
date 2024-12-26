import pickle
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder

# Przygotowanie danych użytkownika i restauracji
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

# Łączenie danych
try:
    merged_data = pd.merge(user_history, restaurant_attributes, on="restaurantId")
    print("Dane zostały pomyślnie połączone.")
except KeyError as e:
    print(f"Błąd podczas łączenia danych: {e}")
    raise

# Kodowanie zmiennych kategorycznych
try:
    le_food = LabelEncoder()
    merged_data["foodType"] = le_food.fit_transform(merged_data["foodType"])
    le_user = LabelEncoder()
    merged_data["userEmail"] = le_user.fit_transform(merged_data["userEmail"])
    print("Kodowanie zmiennych kategorycznych zakończone pomyślnie.")
except Exception as e:
    print(f"Błąd podczas kodowania danych: {e}")
    raise

# Usuwanie niepotrzebnych kolumn
columns_to_drop = ["restaurantName", "lastOrder", "preferredFoodType"]

# Sprawdzenie, które kolumny istnieją w DataFrame
columns_to_drop = [col for col in columns_to_drop if col in merged_data.columns]

# Przygotowanie danych wejściowych (X) i etykiet (y)
try:
    X = merged_data.drop(columns=columns_to_drop + ["userReviewRating"])
    y = merged_data["userReviewRating"]
    print("Przygotowanie danych wejściowych i etykiet zakończone pomyślnie.")
except KeyError as e:
    print(f"Błąd podczas usuwania kolumn: {e}")
    raise

# Podział danych
try:
    train_data, test_data, train_labels, test_labels = train_test_split(X, y, test_size=0.2, random_state=42)
    print("Podział danych na zbiór treningowy i testowy zakończony pomyślnie.")
except ValueError as e:
    print(f"Błąd podczas podziału danych: {e}")
    raise

# Trenowanie modelu
try:
    model = RandomForestClassifier(random_state=42)
    model.fit(train_data, train_labels)
    print("Model został wytrenowany.")
except Exception as e:
    print(f"Błąd podczas trenowania modelu: {e}")
    raise

# Zapisywanie modelu i encoder'a
try:
    with open('restaurant_recommender.pkl', 'wb') as model_file:
        pickle.dump(model, model_file)
    with open('le_food.pkl', 'wb') as encoder_file:
        pickle.dump(le_food, encoder_file)
    with open('le_user.pkl', 'wb') as encoder_file:
        pickle.dump(le_user, encoder_file)
    print("Model i encoder zostały zapisane pomyślnie.")
except Exception as e:
    print(f"Błąd podczas zapisywania modelu: {e}")
    raise
