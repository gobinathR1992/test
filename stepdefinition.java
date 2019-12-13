package stepdefinition;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;

import MongoDB.TestStatus;
import automationLib.Driver;
import automationLib.LaunchPega;
import cucumber.api.PendingException;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.When;
import extentmanager.ExtentManager;
import utils.BaseLogger;
import utils.CustomException;
import utils.ErrorLogger;
import utils.SeleniumUtilities;
import utils.Utilities;

public class stepdefinition {

	ErrorLogger err=new ErrorLogger();
	BaseLogger blogger = new BaseLogger();
	Date date = new Date();
	public static int executionTime;


	String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(date);
	public static long startTime, endtime;

	public  String name="";
	ExtentReports reports = ExtentManager.getInstance();
	TestStatus mongotest; 
	public static ExtentTest  logger;
	public static boolean isServicedown=false;
	public static boolean isDataIssue=false;
	SeleniumUtilities utils ;
	Utilities comnutils;
	CustomException exptn;	
	WebDriver driver = Driver.getPgDriver();
	Actions action;	
	public static ArrayList<String> screenshotpath=new ArrayList<String>();
	DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	@Before
	public void before(Scenario scenario) throws Exception
	{
		System.out.println("Scenario ID " +scenario.getSourceTagNames());
		System.out.println("Scenario Name" +scenario.getName());


		String temp=scenario.getId().split(";")[1];


		Collection <String>  tagnames=scenario.getSourceTagNames();
		ExtentManager.setTagNames(tagnames);	
		name=ExtentManager.getReportName();

		if (name.length()<1)
			name=temp.replaceAll("-"," ");				

		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		startTime = System.nanoTime();
		System.out.println("Start time#####" +startTime);
		logger=reports.createTest(name,temp.replaceAll("-"," "));
		this.comnutils = new Utilities();
		String browser =  comnutils.getPropertyvalue("browser");
		String runtype = comnutils.getPropertyvalue("runtype");
		String ip = "Local";
		if (runtype.equalsIgnoreCase("Grid")){
			ip = comnutils.getPropertyvalue("ip");

		}

		Driver.setPgDriver(browser,runtype,ip);

		if (runtype.equalsIgnoreCase("Grid")){
			String scenarioID = scenario.getId(); 
			System.out.println("#SCENARIO#" + scenarioID + "#BEFORE# Scenario ID - " + scenario.getId());
			System.out.println("#SCENARIO#" + scenarioID + "#BEFORE# Scenario Name - " + scenario.getName());
			System.out.println("#SCENARIO#" + scenarioID + "#BEFORE# Scenario Status - " + scenario.getStatus());
		}


		LaunchPega lnchpega = new LaunchPega();
		String env = System.getProperty("TEST_ENVIRONMENT");
		try{ if(!env.equals(null)){
			System.out.println("From Bamboo Variable");
			lnchpega.launchPega(env);}
		else{
			System.out.println("if failed taking from property file");
			lnchpega.launchPega(comnutils.getPropertyvalue("environment"));  
		}  }catch(NullPointerException e)  {
			lnchpega.launchPega(comnutils.getPropertyvalue("environment"));
		}   

		ExtentManager.setTeststatus("Running");

		this.utils = new SeleniumUtilities();    
		this.action=new Actions(Driver.getPgDriver()); 
		isServicedown=false;
		executionTime=300;
	}


	@After
	public void afterScenario(Scenario scenario) {
		if(ExtentManager.getTeststatus()=="Running"){
			ExtentManager.setTeststatus("Pass");
			ExtentManager.setExecutionTime(executionTime);
		}
		reports.flush();
		endtime = System.nanoTime();

		System.out.println("<BuildNumber>"+System.getProperty("TEST_BUILD")+"</BuildNumber>"+"<BuildStartTime>"+System.getProperty("TEST_EXECUTION_REPORT")+"</BuildStartTime>"
				+"<TestID>"+ExtentManager.getReportName()+"</TestID>"+"<ModuleName>"+ExtentManager.getTagNames()+"</ModuleName>"
				+"<TestStatus>"+ExtentManager.getTeststatus()+
				"</TestStatus>"+"<TestExecutionTime>"+executionTime+
				"</TestExecutionTime>"+"<TestElapsedtime>"+((endtime-startTime)/1000000000)+"</TestElapsedtime>"
				+"<TestReport>https://va10n40601.wellpoint.com/public/SC-QA/" + System.getProperty("TEST_TYPE") + "TestExecutionResults/" + System.getProperty("TEST_NAME") + "/" + System.getProperty("TEST_ENVIRONMENT") + "/" + System.getProperty("TEST_EXECUTION_REPORT") + "_" + System.getProperty("TEST_BUILD") + "/DetailedReport/" + ExtentManager.getReportName() + ".html" + "</TestReport>"   
				);

		String scenarioID = scenario.getId();  
		System.out.println("#SCENARIO# " + scenarioID + " #AFTER# Scenario ID - " + scenario.getId());
		System.out.println("#SCENARIO# " + scenarioID + " #AFTER# Scenario Name - " + scenario.getName());
		System.out.println("#SCENARIO# " + scenarioID + " #AFTER# Scenario Status - " + scenario.getStatus());

		try {
			Driver.getPgDriver().close();
			System.out.println("#SCENARIO# " + scenarioID + " #AFTER# Browser Closed.");

			Driver.getPgDriver().quit();
			System.out.println("#SCENARIO# " + scenarioID + " #AFTER# Driver Quit.");

			if(comnutils.getPropertyvalue("runtype").equals("Grid")) {
				try {
					Driver.customServlet.terminatePirateProcess();
					System.out.println("#SCENARIO# " + scenarioID + " #AFTER# Process(s) terminated.");
				}catch(NullPointerException e) {
					System.out.println("Driver NullPointer: "+e);
				}
			}

			System.out.println("#SCENARIO# " + scenarioID + " #AFTER# Scenario execution is complete.");

			if(comnutils.getPropertyvalue("runtype").equals("Grid"))

			{

				Driver.getPgDriver().close();
				System.out.println("#SCENARIO#" + scenarioID + "#AFTER# Browser Closed.");

				Driver.getPgDriver().quit();
				System.out.println("#SCENARIO#" + scenarioID + "#AFTER# Driver Quit.");

				Driver.customServlet.terminatePirateProcess();
				System.out.println("#SCENARIO#" + scenarioID + "#AFTER# Process(s) terminated.");

				System.out.println("#AFTER SCENARIO#" + scenarioID + "# Scenario execution is complete.");
			}
		}catch(WebDriverException e) {
			System.out.println("WebDriverException: "+e);
		}
	}

	public String takescreenshot(String methodname,String classname) {

		String loc=null;

		try{
			if(screenshotpath.size()==0) screenshotpath.add("Header");
			String currentpage,currentaction;

			String currenttestcase = name;

			String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(date);
			currentpage = classname;
			currentaction = methodname;

			File scrFile = ((TakesScreenshot)Driver.pgDriver).getScreenshotAs(OutputType.FILE);

			String reportName = System.getProperty("TEST_EXECUTION_REPORT");
			String buildNumber = System.getProperty("TEST_BUILD");
			String reportLocation = System.getProperty("user.dir");
			System.out.println("reportLocation"+reportLocation);
			try{
				if (!reportName.isEmpty() && !buildNumber.isEmpty()) {
					reportLocation = reportLocation + "//TestExecutionRecords//" + buildNumber + "//" + reportName + "_" + buildNumber;
					FileUtils.copyFile(scrFile, new File(reportLocation + "/Screenshots/" /*+currenttestcase+"_"*/ + currentpage + "_" + currentaction + "_" + timeStamp + ".png")); 
					loc = "../Screenshots/" /*+currenttestcase+"_"*/ + currentpage + "_" + currentaction + "_" + timeStamp + ".png";
				} else {
					reportLocation = "C://DBGEngine";
					FileUtils.copyFile(scrFile, new File(reportLocation + "/Screenshots/" /*+currenttestcase+"_"*/ + currentpage + "_" + currentaction + "_" + timeStamp + ".png")); 
					loc = "../Screenshots/" /*+currenttestcase+"_"*/ + currentpage + "_" + currentaction + "_" + timeStamp + ".png";
				}

			} catch (NullPointerException e) {
				reportLocation = "C://DBGEngine";
				FileUtils.copyFile(scrFile, new File(reportLocation + "/Screenshots/" /*+currenttestcase+"_"*/ + currentpage + "_" + currentaction + "_" + timeStamp + ".png")); 
				loc = "../Screenshots/" /*+currenttestcase+"_"*/ + currentpage + "_" + currentaction + "_" + timeStamp + ".png";
			}

		} catch (WebDriverException e) {
			System.out.println("Not able to take screen shot due to " + e);
			err.logError(e, methodname);
			ExtentManager.setTeststatus("Warning - Browser Closed unexpectedly");
			throw new PendingException();
		}catch (IOException e) {
			Driver.customServlet.terminatePirateProcess();
			System.out.println("#takescreenshot# Process terminated.");
			System.out.println("Not able to take screen shot due to " + e);
			err.logError(e, methodname);
		}
		System.out.println("Report Location$$$$: "+loc);
		return loc;
	}



	@When("^(.*) \"([^\"]*)\" ((?!data).)* \"([^\"]*)\"(.*)$")
	public void executeMethod(String whatever,String methodname,String Whtever,String classname,String whtever)  throws Throwable{
		String imagepath;
		System.out.println(" without data");
		if( !utils.executeMethod("automationLib."+classname, methodname))
		{

			imagepath=takescreenshot(methodname,classname);
			if (imagepath!=null)
				logger.info(whatever+methodname.substring(0),MediaEntityBuilder.createScreenCaptureFromPath(imagepath).build());
			if(!isServicedown)
				utils.isServiceDown();
			if(!isServicedown)	
				throw new CustomException(err.getErrormessage(),reports,logger,mongotest);
			else{

				logger.warning(extentmanager.ExtentManager.getTeststatus());
				throw new PendingException();
			}
		}
		else
		{
			imagepath=takescreenshot(methodname,classname);
			if (imagepath!=null)
				logger.pass(whatever+methodname.substring(0),MediaEntityBuilder.createScreenCaptureFromPath(imagepath).build());
			else
				logger.pass(whatever+methodname.substring(0, methodname.indexOf("..."))+"passed");
		}	

	}

	@When("^(.*) \"([^\"]*)\" (.*data) \\(([^\"]*)\\) (.*) \"([^\"]*)\"(.*)$")
	public void executeMethod(String whatever, String methodname,String wteverbeforedata,String  arlistconvert,String wtverbeforepage,String classname,String whatevaftrpage) throws IOException,Throwable  {
		String imagepath;
		System.out.println("Data");
		String[] arlist=arlistconvert.split(",");
		if(methodname.toLowerCase().equalsIgnoreCase("LaunchSolutionCentral"))
		{
			System.out.println("Skipping Launch New Co Application");
		}
		else

		{
			if( !utils.executeMethod("automationLib."+classname, methodname, arlist))
			{
				imagepath=takescreenshot(methodname,classname);
				if (imagepath!=null)
					logger.info(whatever+arlistconvert+methodname.substring(0),MediaEntityBuilder.createScreenCaptureFromPath(imagepath).build());
				if(!isServicedown)
					utils.isServiceDown();
				if(!isServicedown)	
					throw new CustomException(err.getErrormessage(),reports,logger,mongotest);
				else{
					logger.warning(extentmanager.ExtentManager.getTeststatus());
					throw new PendingException();
				}

			}
			else
			{
				imagepath=takescreenshot(methodname,classname);
				if (imagepath!=null)
					logger.pass(whatever+arlistconvert+methodname.substring(0),MediaEntityBuilder.createScreenCaptureFromPath(imagepath).build());
				else
					logger.pass(whatever+arlistconvert+methodname.substring(0, methodname.indexOf("..."))+"passed");
			}


		}
	}

	@When("(.*data) \\(([^\"]*)\\)([^.*:)\"]*)([^\"]*)$")
	public void execute(String wtver,String arlistconvert, String wat,String methodname) throws Exception{

		System.out.println("Entry");
		String imagepath;		
		String[] arlist=arlistconvert.split(",");
		String page = methodname.substring(methodname.indexOf("...")+3, methodname.indexOf(":"));
		String method = methodname.substring(methodname.indexOf(":")+1);
		System.out.println("Page: " + page + "method: " + method);
		this.utils = new SeleniumUtilities();
		this.action=new Actions(Driver.getPgDriver());
		if( !utils.executeMethod("automationLib."+page, method, arlist))
		{
			imagepath=takescreenshot(method,page);
			if (imagepath!=null)
				logger.info(wtver+arlistconvert+methodname.substring(0, methodname.indexOf("...")),MediaEntityBuilder.createScreenCaptureFromPath(imagepath).build());
			if(!isServicedown)
				utils.isServiceDown();
			if(!isServicedown)	
				throw new CustomException(err.getErrormessage(),reports,logger,mongotest);
			else{
				ExtentManager.setTeststatus("Warning");
				logger.warning(extentmanager.ExtentManager.getTeststatus());
				System.out.println(ExtentManager.getTeststatus());
				throw new PendingException();
			}

		}
		else
		{
			imagepath=takescreenshot(method,page);
			if (imagepath!=null)
				logger.pass(wtver+arlistconvert+methodname.substring(0, methodname.indexOf("...")),MediaEntityBuilder.createScreenCaptureFromPath(imagepath).build());
			else
				logger.pass(wtver+arlistconvert+methodname.substring(0, methodname.indexOf("..."))+"passed");
		}
	}

	@When("^([^\"\\(]*)$")
	public void execute(String methodname) throws Exception{

		if(methodname.contains("application is opened"))
		{
			System.out.println("Skipping application is opened on the browser"); 
		}else if(methodname.contains("testcase is passed"))
		{
			System.out.println("Testcase passed");
		}
		else{
			System.out.println("Entry new");
			String page = methodname.substring(methodname.indexOf("...")+3, methodname.indexOf(":"));
			String method = methodname.substring(methodname.indexOf(":")+1);
			String imagepath;
			System.out.println("Page " + page + "method  " + method);
			this.utils = new SeleniumUtilities();
			this.action=new Actions(Driver.getPgDriver());
			if( !utils.executeMethod("automationLib."+page, method))
			{

				imagepath=takescreenshot(method,page);

				if (imagepath!=null)
					logger.info(methodname.substring(0, methodname.indexOf("...")),MediaEntityBuilder.createScreenCaptureFromPath(imagepath).build());
				if(!isServicedown)
					utils.isServiceDown();
				if(!isServicedown)	
					throw new CustomException(err.getErrormessage(),reports,logger,mongotest);
				else{
					logger.warning(extentmanager.ExtentManager.getTeststatus());
					System.out.println(ExtentManager.getTeststatus());
					throw new PendingException();
				}

			}
			else
			{
				imagepath=takescreenshot(method,page);
				if (imagepath!=null)
					logger.pass(methodname.substring(0, methodname.indexOf("...")),MediaEntityBuilder.createScreenCaptureFromPath(imagepath).build());
				else
					logger.pass(methodname.substring(0, methodname.indexOf("..."))+"passed");
			}
		}
	}

	@When("I click the element \"([^\"]*)\" on the \"([^\"]*)\" page")
	public void clickAnelemnt(String elename,String pgname) throws Exception
	{
		System.out.println("Entered the when click");
		if(!utils.clickAnelemnt(utils.returnWebelement(pgname, elename), pgname, elename))
			throw new CustomException(err.getErrormessage(),reports,logger);

		takescreenshot(pgname,elename);
	}

	@When("^I enter text \"([^\"]*)\" in \"([^\"]*)\" on the \"([^\"]*)\" page$")
	public void entertextinAnelemnt(String input,String elename,String pgname) throws Exception
	{

		if(! utils.enterTextinAnelemnt(utils.returnWebelement(pgname, elename), input,pgname, elename))
			throw new CustomException(err.getErrormessage(),reports,logger);
		takescreenshot(pgname,elename);
	}


	@When("^I validate the data \"([^\"]*)\" matches with value on the Element \"([^\"]*)\" on the \"([^\"]*)\" page$")
	public void validateDatainAelement(String value,String elename,String pgname) throws Exception
	{

		if(!utils.validateValueinelement(utils.returnWebelement(pgname, elename), value,pgname, elename))
			throw new CustomException(err.getErrormessage(),reports,logger);
		takescreenshot(pgname,elename);
	}
	public boolean isProxyWebelement(WebElement element) {
		if(element.toString().contains("Proxy")){
			return true;
		}

		else{
			return false;
		}
	}

	@When("^I select the value \"([^\"]*)\" on the Element \"([^\"]*)\" on the \"([^\"]*)\" page$")
	public void SelectDatainAelement(String value,String elename,String pgname) throws Exception
	{

		if(!utils.selectDropDownbyVisibleString(utils.returnWebelement(pgname, elename), value,pgname, elename))
			throw new CustomException(err.getErrormessage(),reports,logger);
		takescreenshot(pgname,elename);
	}

	@When("^I press the \"([^\"]*)\" key on the Element \"([^\"]*)\" on the \"([^\"]*)\" page$")
	public void PressKeyonAelement(String value,String elename,String pgname) throws Exception
	{

		if(!utils.pressEnter(utils.returnWebelement(pgname, elename),pgname, elename))
			throw new CustomException(err.getErrormessage(),reports,logger);
		takescreenshot(pgname,elename);
	}

	@When("^I click on the table \"([^\"]*)\" on the position \"([^\"]*)\" on the \"([^\"]*)\" page$")
	public void ClickonTableposition(String elename,String position,String pgname) throws Exception
	{
		String[] positionsplit=position.split(",");	
		if(!utils.clickontablebasedonrowandcolumn(utils.returnWebelement(pgname, elename),Integer.parseInt(positionsplit[0]),Integer.parseInt( positionsplit[1])))
			throw new CustomException(err.getErrormessage(),reports,logger);
		takescreenshot(pgname,elename);
	}
	@When("^I click on the row with value \"([^\"]*)\" on the table \"([^\"]*)\" on the \"([^\"]*)\" page$")	
	public void Clickonrowbasedonvalue(String value,String elename,String pgname) throws Exception
	{
		if(!utils.clickontablerowbasedonvalues(utils.returnWebelement(pgname, elename), value))
			throw new CustomException(err.getErrormessage(),reports,logger);
		takescreenshot(pgname,elename);
	}

}
