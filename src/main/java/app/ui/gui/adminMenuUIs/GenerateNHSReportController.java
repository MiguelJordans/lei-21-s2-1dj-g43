package app.ui.gui.adminMenuUIs;

import app.controller.App;
import app.domain.model.Company;
import app.domain.model.Data;
import app.domain.shared.exceptions.*;
import app.ui.gui.controllers.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class GenerateNHSReportController implements Initializable {

    private SceneController sceneController = SceneController.getInstance();

    private Company company;

    private LocalDate todayDate = LocalDate.now();

    @FXML
    private Button myReturnButtonNHS;
    @FXML
    private Label myLabelNHS;
    @FXML
    private TextField myTextFieldNHS;
    @FXML
    private TextField myTextFieldNHS2;
    @FXML
    private ChoiceBox<String> myChoiceBoxNHS;
    @FXML
    private Button myButton2NHS;
    @FXML
    private DatePicker myDatePicker1;
    @FXML
    private DatePicker myDatePicker2;

    public GenerateNHSReportController() {
        this(App.getInstance().getCompany());
    }

    public GenerateNHSReportController(Company company) {
        this.company = company;
    }

    public void enterData(ActionEvent event) {

        try {

            if (Period.between(getStartDate(), myDatePicker1.getValue()).getDays() < 0) {
                throw new InvalidIntervalOfDatesStartException();
            }

            if (Period.between(myDatePicker2.getValue(), todayDate).getDays() < 0) {
                throw new InvalidIntervalOfDatesEndException();
            }

            if (myChoiceBoxNHS.getValue() == null) {
                throw new ChoiceBoxEmptyException();
            }

            setInformation();
            changeScene(event);

        } catch (ConfidenceLevelInvalidException err1) {
            errorAlert(err1.getMessage());
        } catch (HistoricalDaysEmptyException err2) {
            errorAlert(err2.getMessage());
        } catch (DateEmptyException err3) {
            errorAlert(err3.getMessage());
        } catch (DateInvalidException err4) {
            errorAlert(err4.getMessage());
        } catch (ConfidenceLevelICEmptyException err5) {
            errorAlert(err5.getMessage());
        } catch (ChoiceBoxEmptyException err6) {
            errorAlert(err6.getMessage());
        } catch (HistoricalDaysInvalidException err7) {
            errorAlert(err7.getMessage());
        } catch (RuntimeException err8) {
            errorAlert("Please enter valid information (Don't leave blank containers!)");
        } catch (InvalidIntervalOfDatesStartException err9) {
            errorAlert(err9.getMessage());
        } catch (InvalidIntervalOfDatesEndException err10) {
            errorAlert(err10.getMessage());
        }

    }

    private long getIntervalDate(LocalDate start, LocalDate end) {
        long n;
        return n = Period.between(start, end).getDays();
    }

    public void returnToAdminMenu(ActionEvent event) {
        App app = sceneController.getApp();
        app.doLogout();
        sceneController.switchMenu(event, "/FXML/AdministratorUI.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] choices = {"Simple Linear Regression", "Multi Linear Regression"};
        myChoiceBoxNHS.getItems().addAll(choices);
    }

    private void errorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR!");
        alert.setHeaderText("Invalid format!");
        alert.setContentText(message);
        alert.show();
    }

    private void setInformation() throws DateEmptyException, DateInvalidException, HistoricalDaysInvalidException, HistoricalDaysEmptyException, ConfidenceLevelICEmptyException, ConfidenceLevelInvalidException {

        Data data = company.getData();

        data.setIntervalDates(getIntervalDate(myDatePicker1.getValue(), myDatePicker2.getValue()));
        data.setHistoricalDays(myTextFieldNHS.getText());
        data.setConfidenceLevelIC(myTextFieldNHS2.getText());

    }

    private void changeScene(ActionEvent event) {

        if (myChoiceBoxNHS.getValue() == "Simple Linear Regression") {
            sceneController.switchMenu(event, "/FXML/SimpleLinearRegression.fxml");
        } else {
            sceneController.switchMenu(event, "/FXML/MultiLinearRegression.fxml");
        }
    }

    private LocalDate getStartDate(){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date();
        String todate = dateFormat.format(date);

        int n = Integer.parseInt(myTextFieldNHS.getText());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -n);
        Date todate1 = cal.getTime();
        String fromdate = dateFormat.format(todate1);

        LocalDate date1;

        return    date1 = todate1.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

    }

}