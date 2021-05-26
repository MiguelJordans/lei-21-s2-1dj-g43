package app.domain.model;

import app.domain.shared.Constants;
import app.domain.stores.ParameterCategoryStore;
import app.domain.stores.ParameterStore;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Test {

    private State state;
    private String testCode;
    private String testNhsNumber;
    private String clientCc;
    private TestType testType;
    private ParameterCategoryStore catList;
    private ParameterStore paList;
    private List<TestParameter> testParam;
    private LocalDate createdDate;
    private List<Sample> testSamples;
    private Barcode barcode;
    private Sample sample;

    /**
     * Constructor of the Test object, it call methods on order to validate the NhsNumber, the list of categories and the list of parameters
     *
     * @param testCode      unique code generated automatically
     * @param testNhsNumber unique code that identifies the test
     * @param clientCc      unique code that identifies the client associated with the test
     * @param testType      type of this test
     * @param catList       list of parameters categories that are measured in this test
     * @param paList        list of parameters that are measured in this test
     */
    public Test(String testCode, String testNhsNumber, String clientCc, TestType testType, ParameterCategoryStore catList, ParameterStore paList) {

        checkTestNhsNumberRules(testNhsNumber);
        checkCatList(catList);
        checkPaList(paList);
        this.testCode = testCode;
        this.testNhsNumber = testNhsNumber;
        this.clientCc = clientCc;
        this.testType = testType;
        this.catList = catList;
        this.paList = paList;
        this.createdDate = LocalDate.now();

    }

    /**
     * This method checks if the list of parameters meets the requirements, if not it throws a exception making the execution to stop
     *
     * @param paList list of parameters that are measured in this test
     */
    private void checkPaList(ParameterStore paList) {
        if (paList.isEmpty() || paList == null) {
            throw new IllegalArgumentException("Parameter List must not be empty");
        }

    }

    /**
     * This method checks if the list of parameters categories meets the requirements, if not it throws a exception making the execution to stop
     *
     * @param catList list of parameters categories that are measured in this test
     */
    private void checkCatList(ParameterCategoryStore catList) {
        if (catList.isEmpty() || catList == null) {
            throw new IllegalArgumentException("Category List must not be empty");
        }
    }

    /**
     * This method checks if the list of parameters categories meets the requirements, if not it throws a exception making the execution to stop
     *
     * @param testNhsNumber unique code that identifies the test
     */
    private void checkTestNhsNumberRules(String testNhsNumber) {
        if (testNhsNumber == null) {
            throw new IllegalArgumentException("The NHS Number must exist");
        }

        if (!StringUtils.isAlphanumeric(testNhsNumber)) {
            throw new IllegalArgumentException("The NHS Number must have just alphanumeric characters");
        }

        if (testNhsNumber.length() > Constants.TEST_NHS_CODE) {
            throw new IllegalArgumentException("The NHS Number must have a maximum of 12 characters");
        }

    }

    /**
     * Creates a new TestParameter object for each Parameter in the Parameter list received in the constructor and saves it in a new TestParameter List
     */
    public void addTestParameter() {
        this.testParam = new ArrayList<>();
        for (Parameter p : this.paList.getList()) {
            String code = p.getCode();
            TestParameter tp = new TestParameter(code);

            this.testParam.add(tp);

        }
        changeState(State.CREATED);
    }

    /**
     * Changes the state of the object test by changing the variable state with a value from the enum "State"
     *
     * @param s a value of the enum "State"
     */
    private void changeState(State s) {
        this.state = s;
    }

    public void changeState(String s) {
        changeState(State.contains(s));
    }


    /**
     * @return Test unique NHS number
     */
    public String getTestNhsNumber() {
        return testNhsNumber;
    }

    @Override

    public String toString() {
        return "Test: testCode=" + testCode + ", testNhsNumber=" + testNhsNumber + ", clientCc=" + clientCc + ", testType=" + testType + ", catList=" + catList + ", paList=" + paList;
    }


    public boolean addTestResult(String parameterCode, double result) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        if (!state.equals(State.SAMPLE_COLLECTED)) {
            return false;
        }

        TestParameter pa = null;
        for (TestParameter p : testParam) {
            if (p.getpCode().equals(parameterCode)) {
                pa = p;
            }
        }
        if (pa == null) {
            return false;
        }

        String externalModule = testType.getExternalModule();
        Class<?> oClass = Class.forName(externalModule);
        RefValueAdapter em = (RefValueAdapter) oClass.newInstance();

        pa.addResult(result, em.getRefValue(pa.getpCode()));

        return true;
    }

    public Barcode getBarcode() {
        return barcode;
    }

    public List<TestParameter> getTestParam() {
        return testParam;
    }


    /**
     * This enum represents all the states that the Test object can assume
     */
    enum State {
        CREATED,
        SAMPLE_COLLECTED,
        SAMPLE_ANALYSED,
        DIAGNOSTIC_MADE,
        VALIDATED;

        public static State contains(String state) {

            for (State c : State.values()) {
                if (c.name().equals(state)) {
                    return c;
                }
            }
            return null;
        }

    }
}
