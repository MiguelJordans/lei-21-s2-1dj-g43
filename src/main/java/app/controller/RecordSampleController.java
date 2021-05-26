package app.controller;

import app.domain.mappers.TestListMapper;
import app.domain.mappers.dto.TestDTO;
import app.domain.model.Company;
import app.domain.model.SampleStore;
import app.domain.model.TestType;
import app.domain.model.Test;
import app.domain.stores.TestStore;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;


import java.util.List;

public class RecordSampleController {

    private Company company;
    private TestStore store;
    private TestStore tList;
    private SampleStore sampleList;
    private TestType testType;
    private Test sample;



    public List<TestDTO> tList() {
        this.tList = company.testList();
        TestListMapper typeMapper = new TestListMapper();
        return typeMapper.toDTO(tList);
    }
    public void getLists(){
        this.sampleList = new SampleStore();
    }
    public String getTest(){
        return store.getTest();
    }
    public void createSample(String testID,String barcode) throws ClassNotFoundException, InstantiationException, BarcodeException, IllegalAccessException {
        sampleList.createSample(testID,barcode);
    }


}
