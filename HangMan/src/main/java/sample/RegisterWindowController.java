package sample;

import database.SQLCommands;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterWindowController implements Initializable {
    private Scene mainScene;
    private Stage mainStage;
    @FXML
    private TextField nick;
    @FXML
    private TextField password;
    @FXML
    private TextField applyPass;
    @FXML
    private Text nickMess;
    @FXML
    private Text passMess;
    @FXML
    private Text applyMess;
    @FXML
    private Button button;
    private boolean isNickValid = false;
    private boolean isPasswordValid = false;
   private boolean isApplyPassValid = false;

    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }
    private void showMainScene()
    {
        mainStage.setScene(mainScene);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button.setDisable(true);

        nick.textProperty().addListener((observable,oldValue,newValue )-> {
            if(newValue.isEmpty())
            {
                nickMess.setText("Pole tekstowe jest puste");
                nick.setStyle("-fx-text-box-border: red;");
                isNickValid=false;
            } else if (newValue.length()<4) {
                nickMess.setText("Pole tekstowe jest za krótkie");
                nick.setStyle("-fx-text-box-border: red;");
                isNickValid=false;
            }
            else
            {
                nickMess.setText("Nickname poprawny");
                nick.setStyle("-fx-text-box-border: green;");
                isNickValid=true;
                button.setDisable(!isApplyPassValid || !isPasswordValid);
            }

        });
        password.textProperty().addListener((observable,oldValue,newValue )-> {
            if(newValue.isEmpty())
            {
                passMess.setText("Pole tekstowe jest puste");
                password.setStyle("-fx-text-box-border: red;");
                isPasswordValid=false;
            } else if (newValue.length()<8) {
                passMess.setText("Pole tekstowe jest za krótkie");
                password.setStyle("-fx-text-box-border: red;");
                isPasswordValid=false;
            }
            else if(!newValue.matches(".*[A-Z].*"))
            {
                passMess.setText("Brak dużej litery");
                password.setStyle("-fx-text-box-border: green;");
                isPasswordValid=false;
            }
            else if(!newValue.matches(".*\\d.*"))
            {
                passMess.setText("Brak cyfry");
                password.setStyle("-fx-text-box-border: green;");
                isPasswordValid=false;
            }
            else
            {
                passMess.setText("Hasło poprawny");
                password.setStyle("-fx-text-box-border: green;");
                isPasswordValid=true;
                button.setDisable(!isNickValid || !isApplyPassValid);
            }
        });
        applyPass.textProperty().addListener((observable,oldValue,newValue )-> {
            if(newValue.isEmpty())
            {
                applyMess.setText("Pole tekstowe jest puste");
                applyPass.setStyle("-fx-text-box-border: red;");
                isApplyPassValid=false;
            } else if (!newValue.equals(password.getText())) {
                applyMess.setText("Podane hasła się różnią");
                applyPass.setStyle("-fx-text-box-border: red;");
                isApplyPassValid=false;
            }
            else
            {
                applyMess.setText("Hasła są zgodne");
                applyPass.setStyle("-fx-text-box-border: green;");
                isApplyPassValid=true;
                button.setDisable(!isNickValid || !isPasswordValid);
            }
        });

    }


    public void addAccount() {
        SQLCommands.addAccount(nick.getText(),password.getText());
        showMainScene();
    }
}
