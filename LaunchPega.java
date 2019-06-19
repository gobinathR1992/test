package automationLib;

import utils.BaseLogger;
import utils.SeleniumUtilities;



public class LaunchPega {
	
	String pegaDev  		="https://solutioncentral.dev.va.antheminc.com/public/login.html";
	String pegaSIT1 		="https://solutioncentral.sit.va.antheminc.com/public/login.html";
	String pegaSIT2 		="https://solutioncentral.uat.va.antheminc.com/public/login.html";
	String pegaTraining 	="https://solutioncentral.train.va.antheminc.com/public/login.html";
	String pegaPerformance  ="https://solutioncentral.perf.va.antheminc.com/public/login.html";
	String pegaCI = "https://va10n40610.wellpoint.com:8443/prweb/PRServlet";
	String pegaupgrade1 = "https://solutioncentral.pegasds.va.antheminc.com:1024/prweb/sso";
	String pegaupgrade2= "https://va33dlvpeg302.wellpoint.com:8787/prweb/PRServlet";
	String Offcycle = "https://solutioncentral.pegasd.va.antheminc.com:1024/prweb/sso";
	String SIT_PRIME = "https://va33tlvihs328.wellpoint.com:1024/prweb/sso";
	String HAS ="https://VA33TLVIHS353.wellpoint.com/prweb";
	//String peganewco = "https://va33dlvpeg317.wellpoint.com:8444/prweb/PRServlet/";
	
	String peganewco = "https://va33dlvihs321.wellpoint.com:1025/prweb/";
	
	String puma = "https://beaconqa.corp.agp.ads/prweb/PRServletLDAP2/beEBp4uRVTogorRwSwWqbOtn9IL2fwdI*/!STANDARD";
	String pegaURL;
	String envPath;
	String prod ="https://solutioncentral.antheminc.com/prweb/sso/drc6IS9uuupODtFWD5ZU65zhPdDKtfO30eUPnDeaiMtiFg0AwyCklw%5B%5B*/!STANDARD?";
	String majorsit = "https://solutioncentral.majorsit.antheminc.com:1024/public/login.html";
	String majorstg = "https://solutioncentral.majorstg.antheminc.com:1024/public/login.html";
	String minorsit = "https://solutioncentral.minorsit.antheminc.com/public/login.html";
	String minorstg = "https://solutioncentral.minorstg.antheminc.com/public/login.html";
	
	BaseLogger blogger = new BaseLogger();

	
	public String getPegaURL() {
		return pegaURL;
	}

	public void setPegaURL(String pegaURL) {
		this.pegaURL = pegaURL;
	}
	
	public void launchPega(String env){
		//SeleniumUtilities utils = new SeleniumUtilities();
		if (env.equalsIgnoreCase("SIT"))
			this.setPegaURL(pegaSIT1);
		else if (env.equalsIgnoreCase("UAT"))
			this.setPegaURL(pegaSIT2);
		else if (env.equalsIgnoreCase("CI"))
			this.setPegaURL(pegaCI);
		else if (env.equalsIgnoreCase("Upgrade"))
			this.setPegaURL(pegaupgrade1);
		else if (env.equalsIgnoreCase("TRAIN"))
			this.setPegaURL(pegaTraining);
		else if (env.equalsIgnoreCase("PERF"))
			this.setPegaURL(pegaPerformance);
		else if (env.equalsIgnoreCase("PUMA"))
			this.setPegaURL(puma);		
		else if (env.equalsIgnoreCase("Offcycle"))
			this.setPegaURL(Offcycle);
		else if (env.equalsIgnoreCase("NEWCO"))
			this.setPegaURL(peganewco);
		else if (env.equalsIgnoreCase("prod"))
			this.setPegaURL(prod);
		else if (env.equalsIgnoreCase("SIT_PRIME"))
			this.setPegaURL(SIT_PRIME);
		else if (env.equalsIgnoreCase("HAS"))
			this.setPegaURL(HAS);
		else if (env.equalsIgnoreCase("majorsit"))
			this.setPegaURL(majorsit);
		else if (env.equalsIgnoreCase("majorstg"))
			this.setPegaURL(majorstg);
		else if (env.equalsIgnoreCase("minorsit"))
			this.setPegaURL(minorsit);
		else if (env.equalsIgnoreCase("minorstg"))
			this.setPegaURL(minorstg);


		try {
			Driver.getPgDriver().get(this.getPegaURL());
		    Driver.getPgDriver().manage().window().maximize();
		    String str = Driver.getPgDriver().getCurrentUrl();
		    System.out.println("The current URL is " + str);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			blogger.logserviceDownForBrowserNotInvoked();			
		}
	    //utils.waitForPageLoaded(60);
	}
	
	public String getEnvpath(String env){
		return envPath;              
	}
	
}
