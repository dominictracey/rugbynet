package net.rugby.foundation.topten.server.factory.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList.ITopTenListSummary;
import net.rugby.foundation.topten.model.shared.TopTenItem;
import net.rugby.foundation.topten.model.shared.TopTenList;
import net.rugby.foundation.topten.server.factory.BaseTopTenListFactory;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

public class TestTopTenListFactory extends BaseTopTenListFactory implements ITopTenListFactory {

	protected IPlayerFactory pf;
	protected ICompetitionFactory cf;
	private Random r = new Random();

	private Map<Long,List<ITopTenList>> listMap = new HashMap<Long,List<ITopTenList>>();
	
	@Inject
	public TestTopTenListFactory(IPlayerFactory pf, ICompetitionFactory cf, IMatchGroupFactory mf, ITeamGroupFactory tf, IRoundFactory rf, IPlayerMatchStatsFactory pmsf, IRatingQueryFactory rqf, IPlayerRatingFactory prf, IConfigurationFactory ccf) {
		super(mf,tf, rf, pmsf, rqf, prf, ccf);
		this.pf = pf;
		this.cf = cf;
	}


	public ITopTenList getFromPeristentDatastore(Long id) {
		TopTenList list = new TopTenList();
		list.setId(id);

		// use same players for now
		List<ITopTenItem> items = getItems(list);

//		int i = 1;
//		for (ITopTenItem item : items) {
//			item.setOrdinal(i++);
//		}
		
		if (id.equals(1000L)) {
			list.setCreated(new Date());
			list.setPublished(new Date());
			list.setLive(true);
			list.setTitle("Rugby Championship Round 1 Top Ten");
			list.setContent("What an exciting week of rugby. Lots of great performances to choose from!");
			list.setList(items);
			list.setPrevId(null);
			list.setPrevPublishedId(null);
			list.setNextId(1001L);
			list.setNextPublishedId(1001L);
			list.setCompId(1L);
			return list;
		}

		if (id.equals(1001L)) {
			list.setCreated(new Date());
			list.setPublished(new Date());
			list.setLive(true);
			list.setTitle("Rugby Championship Round 2 Top Ten");
			list.setContent("What an exciting week of rugby. Lots of great performances to choose from!");
			list.setList(getListOfItemsForSimpleList(list));
			list.setPrevId(1000L);
			list.setPrevPublishedId(1000L);
			list.setNextId(1002L);
			list.setNextPublishedId(null);
			list.setCompId(1L);
			return list;
		}

		if (id.equals(1002L)) {
			list.setCreated(new Date());
			list.setLive(false);
			list.setTitle("Rugby Championship Round 3 Top Ten");
			list.setContent("What an exciting week of rugby. Lots of great performances to choose from!");
			list.setPrevId(1001L);
			list.setPrevPublishedId(null);
			list.setNextId(1003L);
			list.setNextPublishedId(null);
			list.setList(items);
			list.setCompId(1L);
			return list;
		}

		if (id.equals(1003L)) {
			list.setCreated(new Date());
			list.setLive(false);
			list.setTitle("Rugby Championship Round 4 Top Ten DRAFT COPY ONLY");
			list.setContent("This is just to allow us to create some content early in the weekend");
			list.setList(items);
			list.setPrevId(1002L);
			list.setPrevPublishedId(null);
			list.setNextId(1004L);
			list.setNextPublishedId(null);
			list.setList(items);
			list.setCompId(1L);
			return list;
		}

		if (id.equals(1004L)) {
			list.setCreated(new Date());
			list.setLive(false);
			list.setTitle("Rugby Championship Round 4 Top Ten");
			list.setContent("Week 4 was just super.");
			list.setList(items);
			list.setPrevId(1003L);
			list.setPrevPublishedId(null);
			list.setNextId(null);
			list.setNextPublishedId(null);
			list.setList(items);
			list.setCompId(1L);
			return list;
		}
		
		if (id > 17000000L) {
			list.setCreated(new Date());
			list.setPublished(new Date());
			list.setQueryId(id - 10000000L);
			list.setLive(true);
			
			list.setContent("What an exciting week of rugby. Lots of great performances to choose from!");
			list.setList(getListOfItemsForSimpleList(list));
			list.setCompId(1L);
			
			position pos = position.NONE;
			String title = "Top Ten ";
			
			// positions
			long q = id-17700000L;
			if (q > 10000L) {
				q -= 10000L;
			}
			
			if (q > 100L) {
				q -= 100L;
			}
			
			pos = position.getAt((int)q);

			
			// criteria
			if (list.getQueryId()/100 - 77100L == 0 || list.getQueryId()/100 - 77000L == 0) {
				title += " Best ";
			} else {
				title += " In Form ";
			}
			
			title += pos.getName() + "s in ";
			if (list.getCompId() == 1L) {
				title += "The Rugby Championship - ";
			}
			
			if (list.getQueryId() > 7700200) {
				title += "Round 2";
			} else {
				title += "Round 1";
			}
			
			for (ITopTenItem i : list.getList()) {
				i.setPosition(pos);
				i.setRating(r.nextInt(1000));
			}
			list.setTitle(title);
			return list;
		}
		return null;
	}

	private List<ITopTenItem> getItems(TopTenList list ) {
		List<ITopTenItem> items = new ArrayList<ITopTenItem>();
		if (list.getId().equals(1000L)) {
			items.add(new TopTenItem(1,1000000L, 9002002L, pf.get(9002002L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L, position.CENTER, list, null, 1500 ));
			items.add(new TopTenItem(2,1000001L, 9001014L, pf.get(9001014L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list, null, 1500 ));
			items.add(new TopTenItem(3,1000002L, 9002012L, pf.get(9002012L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(4,1000003L, 9001002L, pf.get(9001002L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(5,1000004L, 9002017L, pf.get(9002017L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(6,1000005L, 9001009L, pf.get(9001009L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(7,1000006L, 9002006L, pf.get(9002006L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(8,1000007L, 9001019L, pf.get(9001019L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit,", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(9,1000008L, 9002004L, pf.get(9002004L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list, null, 50 ));
			items.add(new TopTenItem(10,1000009L, 9001001L, pf.get(9001001L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list, null, 50));
		} else if (list.getId().equals(1001L)) {
			items.add(new TopTenItem(1,1000010L, 9002007L, pf.get(9002007L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list, null, 1050));
			items.add(new TopTenItem(2,1000011L, 9001018L, pf.get(9001018L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(3,1000012L, 9002006L, pf.get(9002006L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(4,1000013L, 9001002L, pf.get(9001002L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(5,1000014L, 9002017L, pf.get(9002017L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(6,1000015L, 9001009L, pf.get(9001009L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(7,1000016L, 9002001L, pf.get(9002001L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(8,1000017L, 9001019L, pf.get(9001019L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit,", null, 1L, 1L, true, "http://google.com","New Zealand", 9001L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(9,1000018L, 9002004L, pf.get(9002004L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(10,1000019L, 9001001L, pf.get(9001001L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list, null, 500 ));
		} else {
			items.add(new TopTenItem(1,r.nextLong(), 9002007L, pf.get(9002007L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, false, "http://google.com", "Australia", 9002L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(2,r.nextLong(), 9001018L, pf.get(9001018L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com","New Zealand", 9001L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(3,r.nextLong(), 9002006L, pf.get(9002006L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(4,r.nextLong(), 9001002L, pf.get(9001002L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com","New Zealand", 9001L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(5,r.nextLong(), 9002017L, pf.get(9002017L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(6,r.nextLong(), 9001009L, pf.get(9001009L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com","New Zealand", 9001L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(7,r.nextLong(), 9002001L, pf.get(9002001L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", null, 1L, 1L, false, "http://google.com", "Australia", 9002L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(8,r.nextLong(), 9001019L, pf.get(9001019L), "", null, 1L, 1L, false, "http://google.com", "New Zealand", 9001L,  position.CENTER, list, null, 500));
			items.add(new TopTenItem(9,r.nextLong(), 9002004L, pf.get(9002004L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list, null, 500 ));
			items.add(new TopTenItem(10,r.nextLong(), 9001001L, pf.get(9001001L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com","New Zealand", 9001L,  position.CENTER, list, null, 500 ));
		}
		return items;
	}
	
	private List<ITopTenItem> getListOfItemsForSimpleList(TopTenList list) {
		List<ITopTenItem> items = new ArrayList<ITopTenItem>();
		items.add(new TopTenItem(1,1000000L, 9002002L, pf.get(9002002L), "", null, 1L, 1L, true, "http://google.com", "Australia", 9002L, position.CENTER, list, null, 500 ));
		items.add(new TopTenItem(2,1000001L, 9001014L, pf.get(9001014L), "", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.PROP, list, null, 500 ));
		items.add(new TopTenItem(3,1000002L, 9002012L, pf.get(9002012L), "", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.LOCK, list, null, 500 ));
		items.add(new TopTenItem(4,1000003L, 9001002L, pf.get(9001002L), "", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list, null, 500 ));
		items.add(new TopTenItem(5,1000004L, 9002017L, pf.get(9002017L), "", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.WING, list, null, 500 ));
		items.add(new TopTenItem(6,1000005L, 9001009L, pf.get(9001009L), "", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.FLANKER, list, null, 500 ));
		items.add(new TopTenItem(7,1000006L, 9002006L, pf.get(9002006L), "", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.FLANKER, list, null, 500 ));
		items.add(new TopTenItem(8,1000007L, 9001019L, pf.get(9001019L), "", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list, null, 500 ));
		items.add(new TopTenItem(9,1000008L, 9002004L, pf.get(9002004L), "", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.HOOKER, list, null, 500 ));
		items.add(new TopTenItem(10,1000009L, 9001001L, pf.get(9001001L), "", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.SCRUMHALF, list, null, 500 ));

		
		return items;
	}

	public List<ITopTenListSummary> getSummariesForComp(Long compId) {

		return null;

	}


	@Override
	public ITopTenList getLatestForComp(Long compId) {
		// first check memcache
		ITopTenList ttl = super.getLatestForComp(compId);
		if (ttl != null) {
			return ttl;
		} else {
			if (compId.equals(1L)) {
				ttl = get(1000L);

				// find first published
				while (ttl != null && !ttl.getLive()) {
					ttl=get(ttl.getNextId());
				}

				if (ttl != null) {
					// now find last published
					Long nextId = ttl.getNextPublishedId();
					while (nextId != null) {
						ttl = get(nextId);
						nextId = ttl.getNextPublishedId();
					}
				}
				if (ttl != null && ttl.getLive()) {
					setLatestPublishedForComp(ttl,compId);
					return ttl;
				} else 
					return null;
			} else if (compId.equals(2L)) {
				List<ITopTenList> set = null;
				if (!listMap.containsKey(compId)) {
					throw new RuntimeException("something is fucked up");
				} else {
					set = listMap.get(compId);
				}
				int index = set.size()-1;
				ITopTenList cursor =  set.get(index); // last created should be last in list
				//scan back to find the last published one.
				while (cursor != null && !cursor.getLive() && index>0) {
					index--;
					cursor = set.get(index);
					if (cursor.getLive())
						return cursor;
				}
				
				if (index==0 && !cursor.getLive())
					return null; // didn't find
				else
					return cursor;
			} 
			else return null;
		}
	}


	@Override
	public ITopTenList getLastCreatedForComp(Long compId) {
		// first check memcache
		ITopTenList ttl = super.getLastCreatedForComp(compId);
		if (ttl != null) {
			return ttl;
		} else {
			if (compId.equals(1L)) {
				ttl = get(1000L);
				Long nextId = ttl.getNextId();
				while (nextId != null) {
					ttl = get(nextId);
					nextId = ttl.getNextId();
				}
				setLastCreatedForComp(ttl,compId);
				return ttl;
			} else if (compId.equals(2L)) {
				List<ITopTenList> set = null;
				if (!listMap.containsKey(compId)) {
					return null;  // no lists created yet
				} else {
					set = listMap.get(compId);
				}
				return set.get(set.size()-1); // last created should be last in list
			} else {
				return null;
			}
		}

	}


	@Override
	protected void deleteFromPersistentDatastore(ITopTenList list) {
		if (list == null || list.getId() == null) {
			return; // we already don't have it.
		}
		
		if (list.getCompId().equals(2L)) {
			// first confirm we have a list for this comp
			List<ITopTenList> set = null;
			if (!listMap.containsKey(list.getCompId())) {
				throw new RuntimeException("something is fucked up");
			} else {
				set = listMap.get(list.getCompId());
			}
			
			set.remove(list);
		}
	}


	@Override
	protected ITopTenItem putToPersistentDatastore(ITopTenItem item) {
		if (item.getId() == null) {
			item.setId(r.nextLong());
		}
		return item;
	}


	@Override
	protected ITopTenList putToPersistentDatastore(ITopTenList list) {
		if (list.getId() == null) {
			list.setId(r.nextLong());
		}
		
		// first confirm we have a list for this comp
		List<ITopTenList> set = null;
		if (!listMap.containsKey(list.getCompId())) {
			set = new ArrayList<ITopTenList>();
			listMap.put(list.getCompId(), set);
		} else {
			set = listMap.get(list.getCompId());
		}
		
		// now check if the list already has this list and delete it so we can re-add it.
		boolean found = false;
		Iterator<ITopTenList> it = set.iterator();
		
		ITopTenList cursor = null;
		while (it.hasNext() && !found) {
			cursor = it.next();
			if (cursor.getId().equals(list.getId())) {
				found = true;
				break;
			}
		}
		
		if (found)
			set.set(set.indexOf(cursor), list);
		else
			set.add(list);
		
		return list;
	}

}
