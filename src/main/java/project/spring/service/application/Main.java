package project.spring.service.application;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import project.spring.service.models.Room;
import project.spring.service.models.Tenant;
import project.spring.service.models.Type;
import project.spring.service.repositories.HotelRepositoryJdbcTemplateImpl;
import project.spring.service.config.ApplicationConfig;

public class Main extends Application {

    private AnnotationConfigApplicationContext context;

    @Override
    public void start(Stage primaryStage) {
        context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        VBox vbox = new VBox();
        selectButton(vbox);
        Scene scene = new Scene(vbox, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Hotel Management");
        primaryStage.show();
    }

    public void selectButton(VBox vBox) {
        vBox.getChildren().clear();
        Label textLabel = new Label("Выберите действие");
        textLabel.setStyle("-fx-font: 24 bold");
        HBox buttonBox = new HBox(10);
        Button findButton = new Button("Поиск");
        Button printButton = new Button("Вывод");
        Button deleteButton = new Button("Удаление");
        Button saveButton = new Button("Добавление");
        Button rentButton = new Button("Снять комнату");
        Button unrentButton = new Button("Выселиться из комнаты");
        Label resultLabel = new Label();

        buttonBox.getChildren().addAll(findButton, printButton, deleteButton, saveButton, rentButton, unrentButton);
        buttonBox.setAlignment(Pos.TOP_CENTER);

        findButton.setOnAction(e -> find(vBox));
        printButton.setOnAction(e -> print(vBox));
        deleteButton.setOnAction(e -> delete(vBox));
        saveButton.setOnAction(e -> save(vBox));
        rentButton.setOnAction(e -> rent(vBox));
        unrentButton.setOnAction(e -> unrent(vBox));

        vBox.getChildren().addAll(textLabel, buttonBox, resultLabel);
        vBox.setAlignment(Pos.TOP_CENTER);
    }


    private void find(VBox vBox) {
        vBox.getChildren().clear();
        Label textLabel = new Label("Выберите поиск");
        textLabel.setStyle("-fx-font: 24 bold");
        HBox buttonBox = new HBox(10);

        Button findTypeButton = new Button("Найти тип");
        Button findRoomButton = new Button("Найти номер");
        Button findTenantButton = new Button("Найти арендатора");
        Button backButton = new Button("Назад ");
        Label resultLabel = new Label();

        buttonBox.getChildren().addAll(findTypeButton, findRoomButton, findTenantButton, backButton);
        buttonBox.setAlignment(Pos.TOP_CENTER);

        vBox.getChildren().addAll(textLabel, buttonBox, resultLabel);
        vBox.setAlignment(Pos.TOP_CENTER);
        findTypeButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Найти тип");
            dialog.setHeaderText(null);
            dialog.setContentText("Введите ID типа:");

            dialog.showAndWait().ifPresent(idString -> {
                try {
                    Long id = Long.parseLong(idString);
                    HotelRepositoryJdbcTemplateImpl repository = context.getBean(HotelRepositoryJdbcTemplateImpl.class);
                    repository.findTypeById(id).ifPresentOrElse(
                            type -> resultLabel.setText("Найден тип: " + type.getType()),
                            () -> resultLabel.setText("Тип не найден")
                    );
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Некорректный ID");
                }
            });
        });
        findRoomButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Найти номер");
            dialog.setHeaderText(null);
            dialog.setContentText("Введите ID номера:");

            dialog.showAndWait().ifPresent(idString -> {
                try {
                    Long id = Long.parseLong(idString);
                    HotelRepositoryJdbcTemplateImpl repository = context.getBean(HotelRepositoryJdbcTemplateImpl.class);
                    repository.findRoomById(id).ifPresentOrElse(
                            room -> resultLabel.setText("Найден номер: " + room.getType() + " цена: " + room.getPrice() + " размер: " + room.getFootage() + (room.isOccupation() ? " номер занят арендатором с id " + room.getTenantID() : " номер свободен ")),
                            () -> resultLabel.setText("Номер не найден")
                    );
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Некорректный ID");
                }
            });
        });
        findTenantButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Найти арендатора");
            dialog.setHeaderText(null);
            dialog.setContentText("Введите ID арендатора:");

            dialog.showAndWait().ifPresent(idString -> {
                try {
                    Long id = Long.parseLong(idString);
                    HotelRepositoryJdbcTemplateImpl repository = context.getBean(HotelRepositoryJdbcTemplateImpl.class);
                    repository.findTenantById(id).ifPresentOrElse(
                            tenant -> resultLabel.setText("Найден арендатор: " + tenant.getName() + " " + tenant.getSurname() + " номер телефона: " + tenant.getPhoneNumber() + (tenant.getRoom() == null ? " арендатор не снимает комнату " : " арендатор снимает комнату под номером " + tenant.getRoom())),
                            () -> resultLabel.setText("Арендатор не найден")
                    );
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Некорректный ID");
                }
            });
        });
        backButton.setOnAction(e -> selectButton(vBox));
    }

    private void print(VBox vBox) {
        vBox.getChildren().clear();
        Label textLabel = new Label("Выберите что вывести");
        textLabel.setStyle("-fx-font: 24 bold");
        HBox buttonBox = new HBox(10);

        Button printTypeButton = new Button("Вывести типы");
        Button printRoomButton = new Button("Вывести номера");
        Button printTenantButton = new Button("Вывести арендаторов");
        Button backButton = new Button("Назад ");


        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-font: 12 bold");

        buttonBox.getChildren().addAll(printTypeButton, printRoomButton, printTenantButton, backButton);
        buttonBox.setAlignment(Pos.TOP_CENTER);

        vBox.getChildren().addAll(textLabel, buttonBox, resultLabel);
        vBox.setAlignment(Pos.TOP_CENTER);
        printTypeButton.setOnAction(e -> {
            HotelRepositoryJdbcTemplateImpl repository = context.getBean(HotelRepositoryJdbcTemplateImpl.class);
            resultLabel.setText("\t\tТипы:\n" + repository.findAllTypes().toString().replace('[', ' ').replace(']', ' ').replaceAll(",", ""));
        });
        printRoomButton.setOnAction(e -> {
            HotelRepositoryJdbcTemplateImpl repository = context.getBean(HotelRepositoryJdbcTemplateImpl.class);
            resultLabel.setText("\t\t\t\t\tНомера:\n" + repository.findAllRooms().toString().replace('[', ' ').replace(']', ' ').replaceAll(",", ""));
        });
        printTenantButton.setOnAction(e -> {
            HotelRepositoryJdbcTemplateImpl repository = context.getBean(HotelRepositoryJdbcTemplateImpl.class);
            resultLabel.setText("\t\t\t\t\tАрендаторы:\n" + repository.findAllTenants().toString().replace('[', ' ').replace(']', ' ').replaceAll(",", ""));
        });
        backButton.setOnAction(e -> selectButton(vBox));
    }

    private void delete(VBox vBox) {
        vBox.getChildren().clear();
        Label textLabel = new Label("Выберите что удалить");
        textLabel.setStyle("-fx-font: 24 bold");
        HBox buttonBox = new HBox(10);

        Button deleteTypeButton = new Button("Удалить тип");
        Button deleteRoomButton = new Button("Удалить номер");
        Button deleteTenantButton = new Button("Удалить арендатора");
        Button backButton = new Button("Назад ");

        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-font: 12 bold");

        buttonBox.getChildren().addAll(deleteTypeButton, deleteRoomButton, deleteTenantButton, backButton);
        buttonBox.setAlignment(Pos.TOP_CENTER);

        vBox.getChildren().addAll(textLabel, buttonBox, resultLabel);
        vBox.setAlignment(Pos.TOP_CENTER);

        deleteTypeButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Удалить тип");
            dialog.setHeaderText(null);
            dialog.setContentText("Введите ID типа:");

            dialog.showAndWait().ifPresent(idString -> {
                try {
                    Long id = Long.parseLong(idString);
                    HotelRepositoryJdbcTemplateImpl repository = context.getBean(HotelRepositoryJdbcTemplateImpl.class);
                    repository.findTypeById(id).ifPresentOrElse(
                            type -> {
                                repository.deleteType(id);
                                resultLabel.setText("Тип удален: " + type.getType());
                            },
                            () -> resultLabel.setText("Тип не найден")
                    );
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Некорректный ID");
                }
            });
        });
        deleteRoomButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Удалить номер");
            dialog.setHeaderText(null);
            dialog.setContentText("Введите ID номера:");

            dialog.showAndWait().ifPresent(idString -> {
                try {
                    Long id = Long.parseLong(idString);
                    HotelRepositoryJdbcTemplateImpl repository = context.getBean(HotelRepositoryJdbcTemplateImpl.class);
                    repository.findRoomById(id).ifPresentOrElse(
                            room -> {
                                repository.deleteRoom(id);
                                resultLabel.setText("Удален номер: " + room.getType() + " цена: " + room.getPrice() + " размер: " + room.getFootage() + (room.isOccupation() ? " номер занят арендатором с id " + room.getTenantID() : " номер свободен "));
                            },
                            () -> resultLabel.setText("Номер не найден")
                    );
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Некорректный ID");
                }
            });
        });
        deleteTenantButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Удалить арендатора");
            dialog.setHeaderText(null);
            dialog.setContentText("Введите ID арендатор:");

            dialog.showAndWait().ifPresent(idString -> {
                try {
                    Long id = Long.parseLong(idString);
                    HotelRepositoryJdbcTemplateImpl repository = context.getBean(HotelRepositoryJdbcTemplateImpl.class);
                    repository.findTenantById(id).ifPresentOrElse(
                            tenant -> {
                                repository.deleteTenant(id);
                                resultLabel.setText("Удален арендатор: " + tenant.getName() + " " + tenant.getSurname() + " номер телефона: " + tenant.getPhoneNumber() + (tenant.getRoom() == 0 ? " арендатор не снимает комнату " : " арендатор снимает комнату под номером " + tenant.getRoom()));
                            },
                            () -> resultLabel.setText("Арендатор не найден")
                    );
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Некорректный ID");
                }
            });
        });
        backButton.setOnAction(e -> selectButton(vBox));
    }

    private void save(VBox vBox) {
        vBox.getChildren().clear();
        Label textLabel = new Label("Выберите, что сохранить");
        textLabel.setStyle("-fx-font: 24 bold");
        HBox buttonBox = new HBox(10);

        Button saveTypeButton = new Button("Сохранить тип");
        Button saveRoomButton = new Button("Сохранить номер");
        Button saveTenantButton = new Button("Сохранить арендатора");
        Button backButton = new Button("Назад");

        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-font: 12 bold");

        buttonBox.getChildren().addAll(saveTypeButton, saveRoomButton, saveTenantButton, backButton);
        buttonBox.setAlignment(Pos.TOP_CENTER);

        vBox.getChildren().addAll(textLabel, buttonBox, resultLabel);
        vBox.setAlignment(Pos.TOP_CENTER);

        saveTypeButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Сохранить тип");
            dialog.setHeaderText(null);
            dialog.setContentText("Введите тип:");

            dialog.showAndWait().ifPresent(typeString -> {
                HotelRepositoryJdbcTemplateImpl repository = context.getBean(HotelRepositoryJdbcTemplateImpl.class);
                Type type = new Type(typeString);
                repository.saveType(type);
                resultLabel.setText("Тип сохранен: " + typeString);
            });
        });

        saveRoomButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Сохранить номер");
            dialog.setHeaderText(null);
            dialog.setContentText("Введите тип, цену, площадь и ID арендатора (через пробел):");
            dialog.showAndWait().ifPresent(input -> {
                String[] parts = input.split(" ");
                if (parts.length == 4 || parts.length == 3) {
                    try {
                        Room room;
                        if (parts.length == 4) {
                            room = new Room(parts[0].trim(), Integer.parseInt(parts[1].trim()), Integer.parseInt(parts[2].trim()), true);
                            room.setTenantID(Long.parseLong(parts[3].trim()));
                        } else {
                            room = new Room(parts[0].trim(), Integer.parseInt(parts[1].trim()), Integer.parseInt(parts[2].trim()), false);
                        }
                        HotelRepositoryJdbcTemplateImpl repository = context.getBean(HotelRepositoryJdbcTemplateImpl.class);
                        repository.saveRoom(room);
                        resultLabel.setText("Номер сохранен: " + room.getType() + " цена: " + room.getPrice() + " размер: " + room.getFootage() + (room.isOccupation() ? " номер занят арендатором с id " + room.getTenantID() : " номер свободен "));
                    } catch (NumberFormatException ex) {
                        resultLabel.setText("Некорректные данные для номера");
                    }
                } else {
                    resultLabel.setText("Введите 4 или 3 значения через пробел");
                }
            });
        });

        saveTenantButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Сохранить арендатора");
            dialog.setHeaderText(null);
            dialog.setContentText("Введите имя, фамилию, номер телефона и ID номера (через пробел):");

            dialog.showAndWait().ifPresent(input -> {
                String[] parts = input.split(" ");
                if (parts.length == 4 || parts.length == 3) {
                    Tenant tenant;
                    if (parts.length == 4) {
                        tenant = new Tenant(parts[0].trim(), parts[1].trim(), parts[2].trim());
                        tenant.setRoom(Integer.parseInt(parts[3].trim()));
                    } else {
                        tenant = new Tenant(parts[0].trim(), parts[1].trim(), parts[2].trim());
                    }
                    HotelRepositoryJdbcTemplateImpl repository = context.getBean(HotelRepositoryJdbcTemplateImpl.class);
                    int result = repository.saveTenant(tenant);
                    if (result == 1) {
                        resultLabel.setText("Номер с таким ID не найден");
                    } else if (result == 2) {
                        resultLabel.setText("Это номер уже занят");
                    } else if (result == 3) {
                        resultLabel.setText("Арендатор с таким номером телефона уже существует");
                    } else {
                        resultLabel.setText("Арендатор сохранен: " + tenant.getName() + " " + tenant.getSurname() + " номер телефона: " + tenant.getPhoneNumber() + (tenant.getRoom() == null ? " арендатор не снимает комнату " : " арендатор снимает комнату под номером " + tenant.getRoom()));
                    }
                } else {
                    resultLabel.setText("Введите все 3 или 4 значения через пробел");
                }
            });
        });
        backButton.setOnAction(e -> selectButton(vBox));
    }

    private void rent(VBox vBox) {
        selectButton(vBox);
        Label resultLabel = new Label();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Снять комнату");
        dialog.setHeaderText(null);
        dialog.setContentText("Введите id номера и id арендатора");
        vBox.getChildren().addAll(resultLabel);
        vBox.setAlignment(Pos.TOP_CENTER);
        dialog.showAndWait().ifPresent(input -> {
            String[] parts = input.split(" ");
            try {
                if (parts.length == 2) {
                    Long roomID = Long.parseLong(parts[0].trim());
                    Long tenantID = Long.parseLong(parts[1].trim());
                    HotelRepositoryJdbcTemplateImpl repository = context.getBean(HotelRepositoryJdbcTemplateImpl.class);
                    repository.findRoomById(roomID).ifPresentOrElse(
                            room -> {
                                if (room.getTenantID() == 0) {
                                    repository.findTenantById(tenantID).ifPresentOrElse(
                                            tenant -> {
                                                if (tenant.getRoom() == 0) {
                                                    repository.rentARoom(roomID, tenantID);
                                                    resultLabel.setText("Арендатор с id = " + tenantID + " снял номер с id " + roomID);
                                                } else {
                                                    resultLabel.setText("Этот арендатор уже снимает номер");
                                                }
                                            },
                                            () -> resultLabel.setText("Арендатор не найден")
                                    );
                                } else {
                                    resultLabel.setText("Этот номер уже занят");
                                }
                            },
                            () -> resultLabel.setText("Номер не найден")
                    );
                } else {
                    resultLabel.setText("Введите 2 значения через пробел");
                }
            } catch (NumberFormatException ex) {
                resultLabel.setText("Некорректный ID");
            }
        });
    }

    private void unrent(VBox vBox) {
        selectButton(vBox);
        Label resultLabel = new Label();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Выселить из комнаты");
        dialog.setHeaderText(null);
        dialog.setContentText("Введите ID арендатора:");
        vBox.getChildren().addAll(resultLabel);

        dialog.showAndWait().ifPresent(idString -> {
            try {
                Long id = Long.parseLong(idString);
                HotelRepositoryJdbcTemplateImpl repository = context.getBean(HotelRepositoryJdbcTemplateImpl.class);
                repository.findTenantById(id).ifPresentOrElse(
                        tenant -> {
                            if (tenant.getRoom() != null) {
                                resultLabel.setText("Арендатор с id = " + tenant.getId() + " выселен из номера с id " + tenant.getRoom());
                                repository.unrentARoom(Long.valueOf(tenant.getId()));
                            } else {
                                resultLabel.setText("Этот арендатор не снимает номер");
                            }
                        },
                        () -> resultLabel.setText("Арендатор не найден")
                );
            } catch (NumberFormatException ex) {
                resultLabel.setText("Некорректный ID");
            }
        });
    }

    @Override
    public void stop() {
        context.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
