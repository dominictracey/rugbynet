package net.rugby.foundation.admin.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;

public class SingleTableStandingsFetcher implements IStandingsFetcher {

	private IRound round = null;
	private ICompetition comp = null;
	private IUrlCacher urlCacher = null;
	private IStandingFactory standingFetcher = null;
	private List<String> standingTablesList = null;
	private String url = null;
	private String StandingsTableLine = "";
	private Map<String,Integer> standingsMap = new HashMap<String,Integer>();
	
	
	//    @Inject
	public SingleTableStandingsFetcher(IStandingFactory sf) {
		this.standingFetcher = sf;
	}

	@Override
	public void setRound(IRound r) {
		this.round = r;
	}

	@Override
	public void setComp(ICompetition c) {
		this.comp = c;
	}

	@Override
	public IStanding getStandingForTeam(ITeamGroup team) {

		assert (comp != null);
		assert (round != null);
		assert (url != null);
		assert (urlCacher != null);
		assert (team != null);
		try {
			urlCacher.setUrl(url);

			if (StandingsTableLine.isEmpty()) {
				processStandingTablesList();
			}

			IStanding standing = standingFetcher.create();
			standing.setRound(round);
			standing.setRoundId(round.getId());

			standing.setTeam(team);
			standing.setTeamId(team.getId());

			searchTeamStanding(standing);
			return standing;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	private void processStandingTablesList() {

		assert (urlCacher != null);
		

		List<String> wholeFile = urlCacher.get();

		for (int i = 0; i < wholeFile.size(); i++) {
			String line = wholeFile.get(i);
			if (line.contains("</thead><tr class=")) {
				StandingsTableLine = line;
				break;
			}
		
		}
		String standingString[] = StandingsTableLine.split("standings-row");
		//Processes the elements of standingString Array to populate standingsMap 
		for (int i=1; i <= standingString.length-1; i++){
			String[] standing = standingString[i].split("\"number\">");
			String[] team = standingString[i].split("\"team-names\">");
			String number = standing[1].substring(0, standing[1].indexOf('<'));
			String name = team[1].substring(0, team[1].indexOf('<'));
			int standingVal = Integer.parseInt(number);
			standingsMap.put(name, standingVal);
		}
		
	}

	private void searchTeamStanding(IStanding standing) {
		//String standingString = standingLine.split("<|/|>")[2];
		standing.setStanding(standingsMap.get(standing.getTeam().getDisplayName()));
		
		//
	//	standing.setStanding(standingVal);
		
//		for (int i = 0; i < standingTablesList.size(); i++) {
//			String curLine = standingTablesList.get(i);
//			ITeamGroup team = standing.getTeam();
//			if (team != null) {
//				String teamName = standing.getTeam().getDisplayName();
//				if (!teamName.equals(null)) {
//					if (curLine.contains(teamName)) {
//						String standingLine = standingTablesList.get(i - 1);
//						if (!standingLine.equals(null)) {
//							try {
//								String standingString = standingLine.split("<|/|>")[2];
//								if (standingString.contains("."))
//								{
//									int size = standingString.length();
//									standingString = standingString.substring(0, size -1);
//								}
//								int standingVal = Integer.parseInt(standingString);
//								standing.setStanding(standingVal);
//								break;
//							} catch (Throwable ex) {
//								Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
//							}
//						}
//					}
//				}
//			}
//		}
	}

	//    private void writeListToFile(List<String> subLines, String filename) {
	//        if (subLines != null && subLines.size() > 0) {
	//            Iterator<String> iter = subLines.iterator();
	//            PrintWriter writer;
	//            try {
	//                writer = new PrintWriter("C:\\users\\seanm\\Development\\JAVA_SRC\\TEMP_FILES\\" + filename + ".html", "UTF-8");
	//                while (iter.hasNext()) {
	//                    writer.println(iter.next());
	//                }
	//                writer.close();
	//            } catch (FileNotFoundException e) {
	//                e.printStackTrace();
	//            } catch (UnsupportedEncodingException e) {
	//                e.printStackTrace();
	//            }
	//        }
	//    }
	

	public IUrlCacher getUc() {
		return urlCacher;
	}

	@Override
	public void setUc(IUrlCacher uc) {
		this.urlCacher = uc;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

}
