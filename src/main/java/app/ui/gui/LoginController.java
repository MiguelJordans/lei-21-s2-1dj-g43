package app.ui.gui;

import app.controller.AuthController;
import app.ui.console.utils.Utils;
import auth.mappers.dto.UserRoleDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class LoginController {
    public Button btnCancel;
    public Button btnLogin;
    public PasswordField passfield;
    public TextField emailfield;
    private AuthController ctrl = new AuthController();
    private SceneController sceneController = SceneController.getInstance();

    public void login(ActionEvent actionEvent) {

        String password = passfield.getText();
        String email = emailfield.getText();

        if(!(doLogin(email,password))) {
            try {
                sceneController.switchMenu(actionEvent,"src/main/resources/FXML/MainScreen.fxml");
            }catch (Exception e){

            }
        } else {

                List<UserRoleDTO> roles = this.ctrl.getUserRoles();
                if ((roles == null) || (roles.isEmpty())) {
                    System.out.println("User has not any role assigned.");
                } else {
                    UserRoleDTO role = selectsRole(roles);
                    if (!Objects.isNull(role)) {
                       switch(role.getDescription()){
                           case "ADMINISTRATOR":
                               try {
                                   sceneController.switchMenu(actionEvent, "/FXML/AdministratorUI.fxml");
                               }catch (IOException e){

                               }
                               break;
                           case "SPECIALISTDOCTOR" :
                               try {
                                   sceneController.switchMenu(actionEvent, "FXML/SpecialistDoctorUI.fxml");
                               }catch (IOException e){

                               }
                               break;
                           case "ClIENT":
                               try {
                                   sceneController.switchMenu(actionEvent, "FXML/ClientUI.fxml");
                               }catch (IOException e){

                               }
                               break;
                           case "RECEPTIONIST":
                               try {
                                   sceneController.switchMenu(actionEvent, "/FXML/ReceptionistUI.fxml");
                               }catch (IOException e){

                               }
                               break;
                           case "LABORATORYCOORDINATOR":
                               try {
                                   sceneController.switchMenu(actionEvent, "/FXML/LaboratoryCoordinatorUI.fxml");
                               }catch (IOException e){

                               }
                               break;
                           case "MEDICALLABTECHNICIIAN":
                               try {
                                   sceneController.switchMenu(actionEvent, "FXML/MedicalLabTechnicianUI.fxml");
                               }catch (IOException e){

                               }
                               break;
                           case "CLINICALCHEMISTRYTECHNOLOGIST":
                               try {
                                   sceneController.switchMenu(actionEvent, "FXML/ClinicalChemistryTechnologistUI.fxml");
                               }catch (IOException e){

                               }
                               break;
                           default:
                               break;
                       }

                    } else {
                        System.out.println("User did not select a role.");
                    }
                }
            }


        }

    public void cancel(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/MainScreen.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    private boolean doLogin(String id, String pwd) {

        int maxAttempts = 3;
        boolean success = false;

        do {
            maxAttempts--;
            success = ctrl.doLogin(id, pwd);
            if (!success) {
                System.out.println("Invalid UserId and/or Password. \n You have  " + maxAttempts + " more attempt(s).");
            }

        } while (!success && maxAttempts > 0);
        return success;
    }

    private UserRoleDTO selectsRole(List<UserRoleDTO> roles) {
        if (roles.size() == 1)
            return roles.get(0);
        else
            return (UserRoleDTO) Utils.showAndSelectOne(roles, "Select the role you want to adopt in this session:");
    }

}
