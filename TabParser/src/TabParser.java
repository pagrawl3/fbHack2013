import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class TabParser {

	static String Track ;//= "wonderwall";
	static String Artist ;//= "oasis";
	static String TabFilePath ;//= "C:\\Users\\Shivam\\workspace\\WebCrawler\\temp\\";
	static String JsonFilePath ;//= "C:\\Users\\Shivam\\workspace\\WebCrawler\\temp\\";
	static int number = 0;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Options options = new Options();
		options.addOption("t", true, "Track Name");
		options.addOption("a", true, "Artist Name");
		options.addOption("d", true, "Tab File Desination");
		options.addOption("j", true, "JSON File Desination");
		
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse( options, args);
		
		if (cmd.hasOption("t")) {
			Track = cmd.getOptionValue("t").toLowerCase();
		}
		if (cmd.hasOption("a")) {
			Artist = cmd.getOptionValue("a").toLowerCase();
		}
		if (cmd.hasOption("d")) {
			TabFilePath = cmd.getOptionValue("d");
		}
		
		if (cmd.hasOption("j")) {
			JsonFilePath = cmd.getOptionValue("j");
		}
		number = ((int)Artist.charAt(0))-((int)'a')+1;
		//System.out.println("Track:"+Track+", Artist:"+Artist+", Number:"+number+", TabFilePath:"+TabFilePath+", JsonFilePath:"+JsonFilePath);
		new TabParser();
	}

	/**
	 * constructor for TabParser
	 * @throws Exception
	 */
	public TabParser() throws Exception {
		getFile();
		int time_signature = getTime_Signature();
		System.out.println(time_signature);
		getDurationList();
	}
	
	/**
	 * gets the guitar pro file for any song
	 * file gets saved to TabFilePath
	 * @throws Exception
	 */
	private void getFile() throws Exception{
		//System.out.println("number:"+number);
		Document doc = Jsoup.connect("http://gtptabs.com/tabs/"
				+number
				+"/"+URLEncoder.encode(Artist, "UTF-8")
				+"/"+URLEncoder.encode(Track, "UTF-8")).timeout(0).get();
		Elements elements = doc.select(".dlFile");
		for (Element ele : elements) {
			String downloadURL = "http://gtptabs.com"+ele.attr("href");
			File myfile = new File(TabFilePath);
			FileUtils.copyURLToFile(new URL(downloadURL), myfile);
		}
	}
	
	/**
	 * Returns the time signature for a giving song
	 * @return
	 * @throws Exception
	 */
	private Integer getTime_Signature() throws Exception{
		JSONObject audio_summary = getAudio_Summary();
		//System.out.println(audio_summary.get("time_signature"));
		String time_signature = audio_summary.get("time_signature").toString();
		return Integer.parseInt(time_signature);
	}
	
	/**
	 * Saves a list of duration and start time for each measure.
	 * The file gets saved to the JsonFilePath
	 * @throws Exception
	 */
	private void getDurationList() throws Exception{
		JSONObject audio_summary = getAudio_Summary();
		String analysis_url = audio_summary.get("analysis_url").toString();
		File fullJSON = File.createTempFile("full", ".json"); 
		//System.out.println("file:"+fullJSON.getTotalSpace());
		FileInputStream inputStream = new FileInputStream(fullJSON);
		FileUtils.copyURLToFile(new URL(analysis_url), fullJSON);
		String everything = IOUtils.toString(inputStream);
		JSONTokener tokener = new JSONTokener(everything);
		JSONObject root = new JSONObject(tokener);
		JSONArray bars = root.getJSONArray("bars");
		
		FileWriter file = new FileWriter(JsonFilePath);
		file.write(bars.toString());
		file.flush();
		file.close();
		//System.out.println(bars.toString());
		//System.out.println(bars.get(1).toString());
	}
	
	/**
	 * returns a summary of all required metadata. This is needed to get more data.
	 * @return
	 * @throws Exception
	 */
	private JSONObject getAudio_Summary() throws Exception{
		String url ="http://developer.echonest.com/api/v4/song/search?api_key=RJU1OKSCJG82R3IW7&format=json&results=1&artist="+Artist+"&title="+Track+"&bucket=audio_summary";
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		StringBuilder builder = new StringBuilder();
		for (String line = null; (line = reader.readLine()) != null;) {
		    builder.append(line).append("\n");
		}
		JSONTokener tokener = new JSONTokener(builder.toString());
		JSONObject root = new JSONObject(tokener);
		JSONObject songs = (JSONObject) root.getJSONObject("response").getJSONArray("songs").get(0);
		JSONObject audio_summary = songs.getJSONObject("audio_summary");//.getJSONObject("analysis_url");
		return audio_summary;
	}
}