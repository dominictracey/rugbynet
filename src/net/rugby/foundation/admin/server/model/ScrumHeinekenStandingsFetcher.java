package net.rugby.foundation.admin.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.IStandingFullFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.IStandingFull;
import net.rugby.foundation.model.shared.ITeamGroup;

public class ScrumHeinekenStandingsFetcher implements IStandingsFetcher {

    protected IRound round = null;
    protected ICompetition comp = null;
    protected IUrlCacher urlCacher = null;
    protected IStandingFullFactory standingFetcher = null;
    protected List<String> standingTablesList = null;
    protected String url = null;

//    @Inject
	public ScrumHeinekenStandingsFetcher(IStandingFullFactory sf) {
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
    public IStandingFull getStandingForTeam(ITeamGroup team) {

        assert (comp != null);
        assert (round != null);
        assert (url != null);
        assert (urlCacher != null);

        try {
            urlCacher.setUrl(url);

            if (standingTablesList == null) {
                processStandingTablesList();
            }

            IStandingFull standing = standingFetcher.create();
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

        if (standingTablesList == null) {
            standingTablesList = new ArrayList<String>();
        }

        List<String> wholeFile = urlCacher.get();

        int poolTableStart = 0;
        int poolTableEnd = 0;
        for (int i = 0; i < wholeFile.size(); i++) {
            String line = wholeFile.get(i);
            if (line.contains("<div id=\"scrumArticlesBoxContent\">")) {
                poolTableStart = i + 1;
            }
            if (line.contains("<div class=\"liveCntSep\"></div>")) {
                poolTableEnd = i;
            }
        }
        while (poolTableStart < poolTableEnd) {
            String addLine = wholeFile.get(poolTableStart);
            if (addLine.contains("<p ")) {
                poolTableStart = poolTableEnd;
                break;
            } else {
                standingTablesList.add(addLine);
                poolTableStart++;
            }
        }
//        writeListToFile(standingTablesList, "StandingsTables5");
    }

    private void searchTeamStanding(IStandingFull standing) {

        for (int i = 0; i < standingTablesList.size(); i++) {
            String curLine = standingTablesList.get(i);
            ITeamGroup team = standing.getTeam();
            if (team != null) {
                String teamName = standing.getTeam().getDisplayName();
                if (!teamName.equals(null)) {
                    if (curLine.contains(teamName)) {
                        String standingLine = standingTablesList.get(i - 1);
                        if (!standingLine.equals(null)) {
                            try {
                                int standingVal = Integer.parseInt(standingLine.split("<|/|>")[2]);
                                standing.setStanding(standingVal);
                                break;
                            } catch (Throwable ex) {
                                Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
                            }
                        }
                    }
                }
            }
        }
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
