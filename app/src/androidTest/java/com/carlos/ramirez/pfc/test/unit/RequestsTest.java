package com.carlos.ramirez.pfc.test.unit;

import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.test.InstrumentationTestCase;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by raddev01 on 06/10/2014.
 */
public class RequestsTest extends InstrumentationTestCase {

/*
    private static final int TEST_DATABASE_CAPACITY = 500;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        setUpEmptyDatabase();
        mockUps();
    }

    public void setUpEmptyDatabase(){
        SQLiteOpenHelper openHelper = new SQLiteOpenHelper(this.getInstrumentation().getTargetContext().getApplicationContext());
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ApplicationController applicationController = new ApplicationController();
        applicationController.setDatabase(db);
        db.delete(Request.TABLE_NAME, null, null);
        db.delete(Jurisdiction.TABLE_NAME, null, null);
        db.delete(Service.TABLE_NAME, null, null);
    }

    public void mockUps(){
        mockUpJurisdiction();
        mockUpService();
        mockUpRequest(0, Request.STATUS_PROGRESS);
    }

    public void mockUpJurisdiction(){
        Jurisdiction currentJurisdiction = new Jurisdiction();
        currentJurisdiction.setJurisdiction_id("ios.test");

        ArrayList<GsonJurisdiction> jurisdictions = new ArrayList<GsonJurisdiction>();
        GsonJurisdiction jurisdiction = new GsonJurisdiction();

        jurisdiction.setName("Contamos Contigo");
        jurisdiction.setKey_name("Madrid, España");
        jurisdiction.setServer_url("https://demo.mejoratuciudad.org");
        jurisdiction.setActive(true);
        jurisdiction.setId("ios.test");
        jurisdiction.setJurisdiction_id("ios.test");
        jurisdiction.setUpdated_datetime(new Date());
        GsonCoordinate position = new GsonCoordinate();
        position.setLat(40.4378271);
        position.setLng(-3.6795367);
        jurisdiction.setPosition(position);

        jurisdictions.add(jurisdiction);

        Jurisdiction.loadBulkData(jurisdictions);
    }

    public void mockUpService(){
        ArrayList<GsonService> services = new ArrayList<GsonService>();
        GsonService service = new GsonService();
        service.setService_id("12");
        service.setService_code("18");
        services.add(service);

        Service.loadBulkData(services, "ios.test");
    }

    public void mockUpRequest(int request_id, String status){

        ArrayList<GsonRequest> gsonRequests = new ArrayList<>();

        GsonRequest gsonRequest = new GsonRequest();
        gsonRequest.setService_request_id(String.valueOf(request_id));
        gsonRequest.setRequest_token("53f3638ddf40f67b4d8b4567");
        gsonRequest.setService_code("18");
        gsonRequest.setService_id("12");

        gsonRequest.setProvider_user("carlos.ramirez@radmas.com");
        gsonRequest.setAddress_string("Calle del Aguacate, 35, 28054 Madrid, Madrid, España");
        gsonRequest.setAgency_responsible("538a1effdf40f61f398b4568");
        gsonRequest.setComments_count(0);
        gsonRequest.setService_notice("notice");
        gsonRequest.setOpen(true);
        gsonRequest.setPhone("635525644");
        gsonRequest.setNotes_count(1);
        gsonRequest.setEmail("carlos.ramirez@radmas.com");
        gsonRequest.setFirst_name("Carlos");
        gsonRequest.setLast_name("Ramírez");
        gsonRequest.setUser_id("232543451234124");
        gsonRequest.setDevice_type("android");
        gsonRequest.setRequested_datetime(new Date(System.currentTimeMillis()));
        gsonRequest.setUpdated_datetime(new Date(System.currentTimeMillis()));
        gsonRequest.setMedia_url("https://www.google.es/?gws_rd=ssl");
        gsonRequest.setUser_id("122929129129");
        gsonRequest.setDescription("Esta farola esta rota");
        gsonRequest.setJurisdiction("ios.test");
        GsonCoordinate gsonPosition = new GsonCoordinate();
        gsonPosition.setLat(40.369736985341);
        gsonPosition.setLng(-3.7429565470701);
        gsonRequest.setPosition(gsonPosition);
        gsonRequest.setStatus(status);
        gsonRequest.setPriority(0);

        gsonRequests.add(gsonRequest);
        Request.loadBulkData(gsonRequests);
    }

    @SmallTest
    public void test01Deleterequests(){
        setUpEmptyDatabase();
        mockUpRequest(0, Request.STATUS_PROGRESS);
        Request.deleteRequests();
        List<Request> requestsFromDatabase = Request.getRequestsWithPredicate(null, null);
        assertEquals(requestsFromDatabase.size(), 0);
    }

    @SmallTest
    public void test02UpdateRequestData(){
        setUpEmptyDatabase();
        mockUps();
        Request request= Request.getRequestWithId("0");

        assertEquals("0", request.getService_request_id());
        assertEquals("53f3638ddf40f67b4d8b4567", request.getRequest_token());
        assertEquals("18", request.getService_code());
        assertEquals("12", request.getService_id());
        assertEquals("Calle del Aguacate, 35, 28054 Madrid, Madrid, España", request.getAddress_string());
        assertEquals("538a1effdf40f61f398b4568", request.getAgency_responsible());
        assertEquals(0, request.getComments_count());
        assertEquals("notice", request.getService_notice());
        assertEquals(true, request.is_public());
        assertEquals("635525644", request.getPhone());
        assertEquals(1, request.getNotes_count());
        assertEquals("carlos.ramirez@radmas.com", request.getEmail());
        assertEquals("Carlos", request.getFirst_name());
        assertEquals("Ramírez", request.getLast_name());
        assertEquals("ios.test", request.getJurisdiction_id());
        assertEquals(Request.STATUS_PROGRESS, request.getStatus());

        GsonRequest gsonRequest = new GsonRequest();

        gsonRequest.setService_request_id("0");
        gsonRequest.setRequest_token("53f3638ddf40f67b4d8b4566");
        gsonRequest.setService_code("17");
        gsonRequest.setService_id("11");
        gsonRequest.setAddress_string("Calle del Aguacate, 35, 28054 Madrid, Madrid, Españ");
        gsonRequest.setAgency_responsible("538a1effdf40f61f398b4567");
        gsonRequest.setComments_count(1);
        gsonRequest.setService_notice("notic");
        gsonRequest.setOpen(false);
        gsonRequest.setPhone("635525643");
        gsonRequest.setNotes_count(0);
        gsonRequest.setEmail("carlos.ramirez@radmas.co");
        gsonRequest.setFirst_name("Carlo");
        gsonRequest.setLast_name("Ramíre");
        gsonRequest.setJurisdiction("ios.tes");
        gsonRequest.setStatus(Request.STATUS_CLOSED);

        request.updateWithData(gsonRequest);

        assertEquals("0", request.getService_request_id());
        assertEquals("18", request.getService_code());
        assertEquals("ios.test", request.getJurisdiction_id());

        assertEquals("53f3638ddf40f67b4d8b4566", request.getRequest_token());
        assertEquals("11", request.getService_id());
        assertEquals("Calle del Aguacate, 35, 28054 Madrid, Madrid, Españ", request.getAddress_string());
        assertEquals("538a1effdf40f61f398b4567", request.getAgency_responsible());
        assertEquals(1, request.getComments_count());
        assertEquals("notic", request.getService_notice());
        assertEquals(false, request.is_public());
        assertEquals("635525643", request.getPhone());
        assertEquals(0, request.getNotes_count());
        assertEquals("carlos.ramirez@radmas.co", request.getEmail());
        assertEquals("Carlo", request.getFirst_name());
        assertEquals("Ramíre", request.getLast_name());
        assertEquals(Request.STATUS_CLOSED, request.getStatus());
    }

    @SmallTest
    public void test03UpdateRequestStatus(){
        setUpEmptyDatabase();
        mockUps();
        Request request= Request.getRequestWithId("0");
        assertEquals(Request.STATUS_PROGRESS, request.getStatus());
        request.setStatus(Request.STATUS_PENDING);
        assertEquals(Request.STATUS_PENDING, Request.getRequestWithId("0").getStatus());
    }

    @SmallTest
    public void test04UpdateRequestProviderEmail(){
        setUpEmptyDatabase();
        mockUps();
        Request request= Request.getRequestWithId("0");
        assertEquals("carlos.ramirez@radmas.com", request.getProvider_user());
        request.setProvider_user("radmas+admin@radmas.com");
        assertEquals("radmas+admin@radmas.com", Request.getRequestWithId("0").getProvider_user());
    }

    @SmallTest
    public void test05CheckRequest(){
        setUpEmptyDatabase();
        mockUps();
        assertTrue(Request.checkRequest("0"));
    }

    @SmallTest
    public void test10GetRequestsWihPredicate(){
        setUpEmptyDatabase();
        mockUps();
        mockUpRequest(1,Request.STATUS_CLOSED);
        mockUpRequest(2,Request.STATUS_PENDING);
        mockUpRequest(3,Request.STATUS_REJECTED);

        FilterRequest filter = new FilterRequest();
        filter.toggleStatus(Request.STATUS_REJECTED);
        filter.toggleStatus(Request.STATUS_CLOSED);
        filter.toggleStatus(Request.STATUS_PENDING);

        GPSTracker tracker = new GPSTracker(this.getInstrumentation().getTargetContext().getApplicationContext());
        Location location = tracker.getLocation();

        assertEquals(Request.getRequestsWithPredicate(location, filter).size(), 1);
    }

    @SmallTest
    public void test11GetRequestFromId(){
        Request request= Request.getRequestWithId("0");
        Service service = Service.getService("18", "ios.test");
        Service service2 = Service.getServiceWithId("12");
        Jurisdiction jurisdiction= Jurisdiction.getJurisdictionWithId("ios.test");
        assertNotNull(service);
        assertNotNull(service2);
        assertNotNull(jurisdiction);
        assertNotNull(request);
        assertEquals("Calle del Aguacate, 35, 28054 Madrid, Madrid, España", request.getAddress_string());
        assertEquals("ios.test", request.getJurisdiction_id());
        assertEquals("538a1effdf40f61f398b4568", request.getAgency_responsible());
        assertEquals(0, request.getComments_count());
        assertEquals(false, request.isMine());
        assertEquals(40.369736985341, request.getLocation_lat());
        assertEquals(-3.7429565470701, request.getLocation_lng());
        assertEquals(0, request.getPriority());
        assertEquals("18", request.getService_code());
        assertEquals("0", request.getService_request_id());
        assertEquals("53f3638ddf40f67b4d8b4567", request.getRequest_token());
        assertEquals(Request.STATUS_PROGRESS, request.getStatus());
        assertEquals("https://www.google.es/?gws_rd=ssl", request.getMedia_url());
    }

    @SmallTest
    public void test12LoadBulkDataWith500Requests(){
        setUpEmptyDatabase();
        mockUps();

        for(int i =0;i<TEST_DATABASE_CAPACITY; i++){
            mockUpRequest(i, Request.STATUS_PROGRESS);
        }
        List<Request> requestsFromDatabase=Request.getRequestsWithPredicate(null, null);
        assertEquals(requestsFromDatabase.size(), TEST_DATABASE_CAPACITY);
    }

    @SmallTest
    public void test13NullRequests() {
        setUpEmptyDatabase();

        Request.loadBulkData(null);

        List<Request> requestsFromDatabase = Request.getRequestsWithPredicate(null, null);
        assertEquals(requestsFromDatabase.size(), 0);
    }

    @SmallTest
    public void test14EmptyRequests(){
        setUpEmptyDatabase();

        ArrayList<GsonRequest> requestList = new ArrayList<>();
        Request.loadBulkData(requestList);

        List<Request> requestsFromDatabase = Request.getRequestsWithPredicate(null, null);
        assertEquals(requestsFromDatabase.size(), 0);
    }

    @Override
    protected void tearDown() throws Exception {
        SQLiteOpenHelper openHelper = new SQLiteOpenHelper(this.getInstrumentation().getTargetContext().getApplicationContext());
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ApplicationController applicationController = new ApplicationController();
        applicationController.setDatabase(db);

        db.delete(Jurisdiction.TABLE_NAME, null, null);
        db.delete(Service.TABLE_NAME, null, null);
        db.delete(Request.TABLE_NAME, null, null);

        super.tearDown();
    }
*/

}
