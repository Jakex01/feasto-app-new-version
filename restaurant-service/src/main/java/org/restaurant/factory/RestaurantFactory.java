package org.restaurant.factory;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Permission;
import org.restaurant.mapstruct.dto.MenuItemMapper;
import org.restaurant.mapstruct.dto.RestaurantLocationMapper;
import org.restaurant.mapstruct.dto.RestaurantMapper;
import org.restaurant.model.LocationEntity;
import org.restaurant.model.MenuItemEntity;
import org.restaurant.model.RestaurantEntity;
import org.restaurant.request.CreateRestaurantRequestDuplicate;
import org.restaurant.response.ImageResponse;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Component
public class RestaurantFactory {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
//    private static final String SERVICE_ACCOUNT_KEY_PATH = getPathToGoogleCredentials();
    private static final String SERVICE_ACCOUNT_KEY_PATH = "";

    private static String getPathToGoogleCredentials() {
//        String path = System.getenv("GOOGLE_CREDENTIALS_PATH");
        String path = "";
        if (path == null || path.isEmpty()) {
            throw new IllegalStateException("Environment variable 'GOOGLE_CREDENTIALS_PATH' is not set or empty.");
        }
        System.out.println("Resolved GOOGLE_CREDENTIALS_PATH: " + path);
        return path;
    }

    public ImageResponse uploadImageToDrive(File file) {
        ImageResponse imageResponse = new ImageResponse();
        try {
            String folderId = "1z1JQI0KnE6Nl_u0NPI8M7kYoW3Y6HPUS";
            Drive drive = createDriveService();
            com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
            fileMetaData.setName(file.getName());
            fileMetaData.setParents(Collections.singletonList(folderId));

            // Przesłanie pliku
            FileContent mediaContent = new FileContent("image/jpeg", file);
            com.google.api.services.drive.model.File uploadedFile = drive.files().create(fileMetaData, mediaContent)
                    .setFields("id")
                    .execute();

            makeFilePublic(drive, uploadedFile.getId());

            String imageUrl = "https://drive.google.com/uc?export=view&id=" + uploadedFile.getId();
            file.delete();

            // Tworzenie odpowiedzi
            imageResponse.setStatus(200);
            imageResponse.setMessage("Image Successfully Uploaded To Drive");
            imageResponse.setUrl(imageUrl);
        } catch (Exception e) {
            System.err.println("Error uploading file to Google Drive: " + e.getMessage());
            imageResponse.setStatus(500);
            imageResponse.setMessage(e.getMessage());
        }
        return imageResponse;
    }

    // Funkcja ustawiająca uprawnienia publiczne dla pliku
    private void makeFilePublic(Drive driveService, String fileId) throws IOException {
        Permission permission = new Permission();
        permission.setType("anyone"); // Uprawnienia dla każdego
        permission.setRole("reader"); // Tylko do odczytu
        driveService.permissions().create(fileId, permission).execute();
    }


    private Drive createDriveService() throws GeneralSecurityException, IOException {
        System.out.println("here i am: "+ SERVICE_ACCOUNT_KEY_PATH);
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_PATH))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        System.out.println(credential);
        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
                .build();

    }

    public RestaurantEntity createRestaurant(CreateRestaurantRequestDuplicate createRestaurant, String userEmail) throws IOException {
        RestaurantEntity restaurantEntity = RestaurantMapper.INSTANCE
                .restaurantRequestToRestaurantEntity(createRestaurant.restaurantInfo());
        LocationEntity locationEntity = RestaurantLocationMapper.INSTANCE
                .locationRequestToLocationEntity(createRestaurant.restaurantLocation());
        locationEntity.setRestaurant(restaurantEntity);

        List<MenuItemEntity> menuItemEntities = createRestaurant.restaurantMenuItems().stream()
                .map(MenuItemMapper.INSTANCE::menuItemRequestToMenuItemEntity)
                .toList();

        restaurantEntity.setLocations(List.of(locationEntity));
        restaurantEntity.setMenuItems(menuItemEntities);
        restaurantEntity.setManagerEmails(List.of(userEmail));

        return restaurantEntity;
    }


}
