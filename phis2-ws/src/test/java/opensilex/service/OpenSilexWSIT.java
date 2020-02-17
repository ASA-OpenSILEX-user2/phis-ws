//******************************************************************************
//                                DataSourceWSIT.java
// SILEX-PHIS
// Copyright © INRA 2020
// Creation date: Jan 27, 2020
// Contact: Expression userEmail is undefined on line 6, column 15 in file:///home/training/opensilex/phis-ws/phis2-ws/licenseheader.txt., anne.tireau@inra.fr, pascal.neveu@inra.fr
//******************************************************************************
package opensilex.service;

import com.google.gson.internal.LinkedTreeMap;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import opensilex.service.authentication.TokenResponseStructure;
import opensilex.service.model.Contact;
import opensilex.service.resource.dto.TokenDTO;
import opensilex.service.resource.dto.UserDTO;
import opensilex.service.resource.dto.experiment.ExperimentPostDTO;
import opensilex.service.resource.dto.group.GroupPostDTO;
import opensilex.service.resource.dto.project.ProjectPostDTO;
import opensilex.service.result.Result;
import opensilex.service.result.ResultForm;
import opensilex.service.view.brapi.Metadata;
import opensilex.service.view.brapi.Pagination;
import org.apache.commons.codec.digest.DigestUtils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;


/**
 *
 * @author training
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OpenSilexWSIT extends InternalProviderIntegrationTestHelper {

    private static final SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy_MM_dd__HH-mm-ss");
    private static final Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());
    private static final String timeStamp = dateFormater.format(startTimestamp);

    private static final String emailUser = "it-user_" + timeStamp + "@opensilex.org";

    private static final String contactType_SCIENTIFIC_CONTACT = "http://www.opensilex.org/vocabulary/oeso/#ScientificContact";
    private static final String contactType_PROJECT_COORDINATOR = "http://www.opensilex.org/vocabulary/oeso/#ProjectCoordinator";
    private static final String contactType_ADMINISTRATIVE_CONTACT = "http://www.opensilex.org/vocabulary/oeso/#AdministrativeContact";

    private static String TOKKEN;
    private static String itProjectURI;//that will be generated in Itest _1_02_groups_01_POST_ITest
    private static String itExperimentURI;//that will be generated in Itest _3_02_experiments_01_POST_ITest

    public void getToken() {
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setGrant_type("password");
        tokenDTO.setClient_id("123");
        tokenDTO.setUsername("admin@opensilex.org");
        tokenDTO.setPassword("21232f297a57a5a743894a0e4a801fc3");

        Entity<TokenDTO> tokenEntity = Entity.entity(tokenDTO, MediaType.APPLICATION_JSON_TYPE);

        Response response = target("/brapi/v1/token")
                .request()
                .post(tokenEntity);

        TokenResponseStructure token = response.readEntity(TokenResponseStructure.class);
        String accessToken = token.getAccess_token();

        if (!accessToken.equals("")) {
            this.TOKKEN = accessToken;
        } else {
            this.TOKKEN = "";
        }

    }

    @Test
    public void _0_01_getToken_ITest() {

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        preTestCaseTrace(nameofCurrMethod);

        this.getToken();
        System.out.println("-----------Token: " + this.TOKKEN);

        assertFalse("Token must be setted", this.TOKKEN.equals(""));

        postTestCaseTrace(nameofCurrMethod);

    }

    @Test
    public void _1_01_users_01_POST_ITest() {

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        preTestCaseTrace(nameofCurrMethod);

        ArrayList<UserDTO> users = new ArrayList<>();
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(emailUser);

        String passwordITUser = "azerty";
        String pwdHash = DigestUtils.md5Hex(passwordITUser);
        userDTO.setPassword(pwdHash);

        userDTO.setFirstName("firstName_IT_User");
        userDTO.setFamilyName("FamilyName_IT_User");
        userDTO.setAddress("address_IT_User");
        userDTO.setPhone("000000000");
        userDTO.setAffiliation("affiliation_IT_User");
        userDTO.setOrcid(null);
        userDTO.setAdmin("true");
        userDTO.setGroupsUris(new ArrayList<>());

        users.add(userDTO);
        Entity< ArrayList<UserDTO>> usersEntity = Entity.entity(users, MediaType.APPLICATION_JSON_TYPE);

        Response response = target("/users")
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.TOKKEN)
                .post(usersEntity);

        System.out.println("--------------- Response: \n" + response);

        ResultForm resultForm = response.readEntity(ResultForm.class);

//        String  stringResponse = respnseFormPostVariables.toString();
//        Metadata metadata = respnseFormPostVariables.getMetadata();
//        List<String> dataFiles = metadata.getDatafiles();
//        Pagination pagination = metadata.getPagination();
//        Integer totalCount = pagination.getTotalCount();
//        
//        System.out.println();        
//
//        assertTrue("Nb users should be > 0 ", totalCount > 0);
        postTestCaseTrace(nameofCurrMethod);

    }

    @Test
    public void _1_01_users_02_GET_ITest() {

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        preTestCaseTrace(nameofCurrMethod);

        Response response = target("/users")
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.TOKKEN)
                .get();
        System.out.println("--------------- Response: \n" + response);

        ResultForm resultForm = response.readEntity(ResultForm.class);

        String stringResponse = resultForm.toString();
        Metadata metadata = resultForm.getMetadata();
        List<String> dataFiles = metadata.getDatafiles();
        Pagination pagination = metadata.getPagination();
        Integer totalCount = pagination.getTotalCount();

        System.out.println("--------------- totalCount!!!!!!!!!! " + totalCount);

        assertTrue("Nb users should be > 0 ", totalCount > 0);

        postTestCaseTrace(nameofCurrMethod);

    }

    @Test
    public void _1_02_groups_01_POST_ITest() {

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        preTestCaseTrace(nameofCurrMethod);

        ArrayList<GroupPostDTO> groups = new ArrayList<>();
        GroupPostDTO groupDTO = new GroupPostDTO();

        groupDTO.setName("group_IT_" + timeStamp);
        groupDTO.setLevel("Owner");
        groupDTO.setDescription("Description of ggroup_IT_" + timeStamp);

        ArrayList<String> usersEmails = new ArrayList<>();
        usersEmails.add(emailUser);
        groupDTO.setUsersEmails(usersEmails);

        groups.add(groupDTO);
        Entity< ArrayList<GroupPostDTO>> groupEntity = Entity.entity(groups, MediaType.APPLICATION_JSON_TYPE);

        Response response = target("/groups")
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.TOKKEN)
                .post(groupEntity);

        System.out.println("--------------- Response: \n" + response);

        ResultForm resultForm = response.readEntity(ResultForm.class);

//        String  stringResponse = respnseFormPostVariables.toString();
        Metadata metadata = resultForm.getMetadata();
        List<String> dataFiles = metadata.getDatafiles();
        Integer sizeDataFiles = dataFiles.size();

        assertTrue("URI of created groups must be returned", sizeDataFiles > 0);
        postTestCaseTrace(nameofCurrMethod);

    }

    @Test
    public void _1_02_groups_02_GET_ITest() {

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        preTestCaseTrace(nameofCurrMethod);

        Response response = target("/groups")
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.TOKKEN)
                .get();
        System.out.println("--------------- Response: \n" + response);

        ResultForm resultForm = response.readEntity(ResultForm.class);

        Metadata metadata = resultForm.getMetadata();
//        List<String> dataFiles = metadata.getDatafiles();
        Pagination pagination = metadata.getPagination();
        Integer totalCount = pagination.getTotalCount();

        System.out.println("--------------- totalCount!!!!!!!!!! " + totalCount);

        assertTrue("Nb groups should be > 0 ", totalCount > 0);

        postTestCaseTrace(nameofCurrMethod);

    }

    @Test
    public void _2_projects_01_POST_ITest() {

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        preTestCaseTrace(nameofCurrMethod);

        ArrayList<ProjectPostDTO> projects = new ArrayList<>();
        ProjectPostDTO projectDTO = new ProjectPostDTO();

        projectDTO.setName("Project_IT_" + timeStamp);
        projectDTO.setAcronyme("Proj_" + timeStamp);
        projectDTO.setFinancialSupport("financial reference");
        projectDTO.setDescription("This project is about ...");
        projectDTO.setDateStart("2015-07-07");
        projectDTO.setDateEnd("2016-07-07");
        projectDTO.setKeywords("keyword1 keyword2");
        projectDTO.setWebsite("http://example.com");

        //SCIENTIFIC_CONTACT
        ArrayList<Contact> usersContact = new ArrayList<>();
        Contact contact = new Contact();
        contact.setEmail(emailUser);
        contact.setType(contactType_SCIENTIFIC_CONTACT);
        usersContact.add(contact);

        //PROJECT_COORDINATOR
        Contact projectCoordinator = new Contact();
        projectCoordinator.setEmail(emailUser);
        projectCoordinator.setType(contactType_PROJECT_COORDINATOR);
        usersContact.add(projectCoordinator);

        //ADMINISTRATIVE_CONTACT
        Contact administrativeContact = new Contact();
        administrativeContact.setEmail(emailUser);
        administrativeContact.setType(contactType_ADMINISTRATIVE_CONTACT);
        usersContact.add(administrativeContact);

        projectDTO.setContacts(usersContact);

        projects.add(projectDTO);

        Entity< ArrayList<ProjectPostDTO>> groupEntity = Entity.entity(projects, MediaType.APPLICATION_JSON_TYPE);

        Response response = target("/projects")
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.TOKKEN)
                .post(groupEntity);

        System.out.println("--------------- Response: \n" + response);

        ResultForm resultForm = response.readEntity(ResultForm.class);

//        String  stringResponse = respnseFormPostVariables.toString();
        Metadata metadata = resultForm.getMetadata();
        List<String> dataFiles = metadata.getDatafiles();
        String uriGeneratedProject = dataFiles.get(0);
        itProjectURI = uriGeneratedProject;
        Integer sizeDataFiles = dataFiles.size();

        assertTrue("URI of created projects must be returned", sizeDataFiles > 0);
        postTestCaseTrace(nameofCurrMethod);

    }

    @Test
    public void _2_projects_02_GET_ITest() {

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        preTestCaseTrace(nameofCurrMethod);

        Response response = target("/projects")
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.TOKKEN)
                .get();
        System.out.println("--------------- Response: \n" + response);

        ResultForm resultForm = response.readEntity(ResultForm.class);

        String stringResponse = resultForm.toString();
        Metadata metadata = resultForm.getMetadata();
        List<String> dataFiles = metadata.getDatafiles();
        Pagination pagination = metadata.getPagination();
        Integer totalCount = pagination.getTotalCount();

        System.out.println("--------------- totalCount!!!!!!!!!! " + totalCount);

        assertTrue("Nb projects should be > 0 ", totalCount > 0);

        postTestCaseTrace(nameofCurrMethod);

    }

    @Test
    public void _3_experiments_01_POST_ITest() {

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        preTestCaseTrace(nameofCurrMethod);

        ArrayList<ExperimentPostDTO> experiments = new ArrayList<>();
        ExperimentPostDTO experimentDTO = new ExperimentPostDTO();

//        experimentDTO.setName("Experiment_IT_" + timeStamp
        experimentDTO.setStartDate("2015-07-07");
        experimentDTO.setEndDate("2015-08-07");
        experimentDTO.setField("field");
        experimentDTO.setCampaign("campain_IT_" + timeStamp);
        experimentDTO.setPlace("place");
        experimentDTO.setComment("Comment IT_experiment_" + timeStamp);
        experimentDTO.setKeywords("keyword1 keyword2");
        experimentDTO.setObjective("Objective experiement_IT_" + timeStamp);
        experimentDTO.setCropSpecies("maize");

//        ArrayList<String> projectsUris = new ArrayList<String>();
//        projectsUris.add(itProjectURI);
        experimentDTO.setProjectsUris(new ArrayList<String>() {
            {
                add(itProjectURI);
            }
        });

        //SCIENTIFIC_CONTACT
        ArrayList<Contact> usersContact = new ArrayList<>();
        Contact contact = new Contact();
        contact.setEmail(emailUser);
        contact.setType(contactType_SCIENTIFIC_CONTACT);
        usersContact.add(contact);

        //PROJECT_COORDINATOR
        Contact projectCoordinator = new Contact();
        projectCoordinator.setEmail(emailUser);
        projectCoordinator.setType(contactType_PROJECT_COORDINATOR);
        usersContact.add(projectCoordinator);

        //ADMINISTRATIVE_CONTACT
        Contact administrativeContact = new Contact();
        administrativeContact.setEmail(emailUser);
        administrativeContact.setType(contactType_ADMINISTRATIVE_CONTACT);
        usersContact.add(administrativeContact);

        experimentDTO.setContacts(usersContact);

        experiments.add(experimentDTO);

        Entity< ArrayList<ExperimentPostDTO>> groupEntity = Entity.entity(experiments, MediaType.APPLICATION_JSON_TYPE);

        Response response = target("/experiments")
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.TOKKEN)
                .post(groupEntity);

        System.out.println("--------------- Response: \n" + response);

        ResultForm resultForm = response.readEntity(ResultForm.class);

        Metadata metadata = resultForm.getMetadata();
        List<String> dataFiles = metadata.getDatafiles();
        String uriGeneratedExperiment = dataFiles.get(0);
        itExperimentURI = uriGeneratedExperiment;
        Integer sizeDataFiles = dataFiles.size();

        assertTrue("URI of created experiments must be returned", sizeDataFiles > 0);
        postTestCaseTrace(nameofCurrMethod);

    }

    @Test
    public void _3_experiments_02_GET_ITest() {

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        preTestCaseTrace(nameofCurrMethod);

        Response response = target("/experiments")
                //                .queryParam("uri", itExperimentURI)
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.TOKKEN)
                .get();
        System.out.println("--------------- Response: \n" + response);

        ResultForm resultForm = response.readEntity(ResultForm.class);

        Metadata metadata = resultForm.getMetadata();
        List<String> dataFiles = metadata.getDatafiles();
        Pagination pagination = metadata.getPagination();
        Integer totalCount = pagination.getTotalCount();

        System.out.println("--------------- totalCount!!!!!!!!!! " + totalCount);

        assertTrue("Nb experiments should be > 0 ", totalCount > 0);

        postTestCaseTrace(nameofCurrMethod);

    }

    @Test
    public void _4_scientificOject_02_GET_ITest() {

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        preTestCaseTrace(nameofCurrMethod);

        Response responseScientificObjects = target("/scientificObjects")
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.TOKKEN)
                .get();

        ResultForm resultForm = responseScientificObjects.readEntity(ResultForm.class);

        String stringResponse = resultForm.toString();
        Metadata metadata = resultForm.getMetadata();
        List<String> dataFiles = metadata.getDatafiles();
        Pagination pagination = metadata.getPagination();
        Integer totalCount = pagination.getTotalCount();

        System.out.println("--------------- totalCount!!!!!!!!!! " + totalCount);

        assertTrue("Nb scientificObjects should be > 0 ", totalCount > 0);

        postTestCaseTrace(nameofCurrMethod);

    }

    @Test
    public void _5_variables_02_GET_ITest() {

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        preTestCaseTrace(nameofCurrMethod);

        Response response = target("/variables")
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.TOKKEN)
                .get();

        System.out.println("--------------- Response: \n" + response);

        ResultForm resultForm = response.readEntity(ResultForm.class);

        String stringResponse = resultForm.toString();
        Metadata metadata = resultForm.getMetadata();
        List<String> dataFiles = metadata.getDatafiles();
        Pagination pagination = metadata.getPagination();
        Integer totalCount = pagination.getTotalCount();

        System.out.println("--------------- totalCount!!!!!!!!!! " + totalCount);

        assertTrue("Nb variables should be > 0 ", totalCount > 0);

        postTestCaseTrace(nameofCurrMethod);

    }

    @Test
    public void _6_dataset_02_GET_ITest() {

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        preTestCaseTrace(nameofCurrMethod);

        String variableUri = "http://www.phenome-fppn.fr/m3p/variable/ev000001";//!!!!!!!!!!

        Response response = target("/data")
                .queryParam("variable", variableUri)
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.TOKKEN)
                .get();

        ResultForm respnseForm = response.readEntity(ResultForm.class);

        Metadata metadata = respnseForm.getMetadata();
        Pagination pagination = metadata.getPagination();
        Integer totalCount = pagination.getTotalCount();

        Result result = respnseForm.getResult();
        ArrayList<LinkedTreeMap> data = (ArrayList<LinkedTreeMap>) result.getData();
        Integer dataSize = data.size();

        System.out.println("--------------- totalCount data for varaiable (" + variableUri + "): " + totalCount);

        assertTrue("totalCount data should be > 0 ", totalCount > 0);
        assertTrue("resultSize should be > 0 ", dataSize > 0);

//        Iterator<LinkedTreeMap> iter = data.iterator(); 
//        while (iter.hasNext()) { 
//            System.out.print(iter.next() + " "); 
//        }         
        postTestCaseTrace(nameofCurrMethod);

    }

    @Test
    public void _7_image_02_GET_ITest() {

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        preTestCaseTrace(nameofCurrMethod);

        String rdfType = "http://www.opensilex.org/vocabulary/oeso#HemisphericalImage";//!!!!!!!!!!!!
        Response response = target("/data/file/search")
                .queryParam("rdfType", rdfType)
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.TOKKEN)
                .get();
        System.out.println("--------------- Response: \n" + response);

        ResultForm resultForm = response.readEntity(ResultForm.class);

        Metadata metadata = resultForm.getMetadata();
//        List<String> dataFiles = metadata.getDatafiles();
//        Pagination pagination = metadata.getPagination();
//        Integer totalCount = pagination.getTotalCount();

//        System.out.println("--------------- totalCount!!!!!!!!!! " + totalCount);
//        assertTrue("Nb users should be > 0 ", totalCount > 0);
        postTestCaseTrace(nameofCurrMethod);

    }



}
