package net.rugby.foundation.topten.server.factory.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
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

	@Inject
	public TestTopTenListFactory(IPlayerFactory pf, ICompetitionFactory cf, IMatchGroupFactory mf, ITeamGroupFactory tf) {
		super(mf,tf);
		this.pf = pf;
		this.cf = cf;
	}


	public ITopTenList getFromPeristentDatastore(Long id) {
		TopTenList list = new TopTenList();
		list.setId(id);

		// use same players for now
		List<ITopTenItem> items = getItems(list);

		int i = 1;
		for (ITopTenItem item : items) {
			item.setOrdinal(i++);
		}
		
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
		return null;
	}

	private List<ITopTenItem> getItems(TopTenList list) {
		List<ITopTenItem> items = new ArrayList<ITopTenItem>();
		if (list.getId().equals(1000L)) {
			items.add(new TopTenItem(1000000L, 9002002L, pf.get(9002002L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L, position.CENTER, list));
			items.add(new TopTenItem(1000001L, 9001014L, pf.get(9001014L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list));
			items.add(new TopTenItem(1000002L, 9002012L, pf.get(9002012L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list));
			items.add(new TopTenItem(1000003L, 9001002L, pf.get(9001002L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list));
			items.add(new TopTenItem(1000004L, 9002017L, pf.get(9002017L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list));
			items.add(new TopTenItem(1000005L, 9001009L, pf.get(9001009L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list));
			items.add(new TopTenItem(1000006L, 9002006L, pf.get(9002006L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list));
			items.add(new TopTenItem(1000007L, 9001019L, pf.get(9001019L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit,", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list));
			items.add(new TopTenItem(1000008L, 9002004L, pf.get(9002004L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list));
			items.add(new TopTenItem(1000009L, 9001001L, pf.get(9001001L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list));
		} else if (list.getId().equals(1001L)) {
			items.add(new TopTenItem(1000010L, 9002007L, pf.get(9002007L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list));
			items.add(new TopTenItem(1000011L, 9001018L, pf.get(9001018L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list));
			items.add(new TopTenItem(1000012L, 9002006L, pf.get(9002006L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list));
			items.add(new TopTenItem(1000013L, 9001002L, pf.get(9001002L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list));
			items.add(new TopTenItem(1000014L, 9002017L, pf.get(9002017L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list));
			items.add(new TopTenItem(1000015L, 9001009L, pf.get(9001009L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list));
			items.add(new TopTenItem(1000016L, 9002001L, pf.get(9002001L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list));
			items.add(new TopTenItem(1000017L, 9001019L, pf.get(9001019L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit,", null, 1L, 1L, true, "http://google.com","New Zealand", 9001L,  position.CENTER, list));
			items.add(new TopTenItem(1000018L, 9002004L, pf.get(9002004L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list));
			items.add(new TopTenItem(1000019L, 9001001L, pf.get(9001001L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list));
		} else {
			items.add(new TopTenItem(r.nextLong(), 9002007L, pf.get(9002007L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, false, "http://google.com", "Australia", 9002L,  position.CENTER, list));
			items.add(new TopTenItem(r.nextLong(), 9001018L, pf.get(9001018L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com","New Zealand", 9001L,  position.CENTER, list));
			items.add(new TopTenItem(r.nextLong(), 9002006L, pf.get(9002006L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list));
			items.add(new TopTenItem(r.nextLong(), 9001002L, pf.get(9001002L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com","New Zealand", 9001L,  position.CENTER, list));
			items.add(new TopTenItem(r.nextLong(), 9002017L, pf.get(9002017L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list));
			items.add(new TopTenItem(r.nextLong(), 9001009L, pf.get(9001009L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com","New Zealand", 9001L,  position.CENTER, list));
			items.add(new TopTenItem(r.nextLong(), 9002001L, pf.get(9002001L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", null, 1L, 1L, false, "http://google.com", "Australia", 9002L,  position.CENTER, list));
			items.add(new TopTenItem(r.nextLong(), 9001019L, pf.get(9001019L), "", null, 1L, 1L, false, "http://google.com", "New Zealand", 9001L,  position.CENTER, list));
			items.add(new TopTenItem(r.nextLong(), 9002004L, pf.get(9002004L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.CENTER, list));
			items.add(new TopTenItem(r.nextLong(), 9001001L, pf.get(9001001L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com","New Zealand", 9001L,  position.CENTER, list));
		}
		return items;
	}
	
	private List<ITopTenItem> getListOfItemsForSimpleList(TopTenList list) {
		List<ITopTenItem> items = new ArrayList<ITopTenItem>();
		items.add(new TopTenItem(1000000L, 9002002L, pf.get(9002002L), "", null, 1L, 1L, true, "http://google.com", "Australia", 9002L, position.CENTER, list));
		items.add(new TopTenItem(1000001L, 9001014L, pf.get(9001014L), "", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.PROP, list));
		items.add(new TopTenItem(1000002L, 9002012L, pf.get(9002012L), "", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.LOCK, list));
		items.add(new TopTenItem(1000003L, 9001002L, pf.get(9001002L), "", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list));
		items.add(new TopTenItem(1000004L, 9002017L, pf.get(9002017L), "", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.WING, list));
		items.add(new TopTenItem(1000005L, 9001009L, pf.get(9001009L), "", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.FLANKER, list));
		items.add(new TopTenItem(1000006L, 9002006L, pf.get(9002006L), "", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.FLANKER, list));
		items.add(new TopTenItem(1000007L, 9001019L, pf.get(9001019L), "", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.CENTER, list));
		items.add(new TopTenItem(1000008L, 9002004L, pf.get(9002004L), "", null, 1L, 1L, true, "http://google.com", "Australia", 9002L,  position.HOOKER, list));
		items.add(new TopTenItem(1000009L, 9001001L, pf.get(9001001L), "", null, 1L, 1L, true, "http://google.com", "New Zealand", 9001L,  position.SCRUMHALF, list));

		
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
				if (ttl != null && ttl.getLive())
					return ttl;
				else 
					return null;
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
			}
			else {
				return null;
			}
		}

	}


	@Override
	protected void deleteFromPersistentDatastore(ITopTenList list) {
		//no-op

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
		return list;
	}

}
