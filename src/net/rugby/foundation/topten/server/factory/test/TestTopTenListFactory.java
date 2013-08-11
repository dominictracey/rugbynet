package net.rugby.foundation.topten.server.factory.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
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

	@Inject
	public TestTopTenListFactory(IPlayerFactory pf, ICompetitionFactory cf) {
		this.pf = pf;
		this.cf = cf;
	}
	

	public ITopTenList get(Long id) {
		TopTenList list = new TopTenList();
		list.setId(id);
		
		// use same players for now
		List<ITopTenItem> items = getItems(list);
		
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
			list.setList(items);
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
		items.add(new TopTenItem(1000000L, 9002002L, pf.getById(9002002L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia"));
		items.add(new TopTenItem(1000001L, 9001014L, pf.getById(9001014L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand"));
		items.add(new TopTenItem(1000002L, 9002012L, pf.getById(9002012L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia"));
		items.add(new TopTenItem(1000003L, 9001002L, pf.getById(9001002L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand"));
		items.add(new TopTenItem(1000004L, 9002017L, pf.getById(9002017L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia"));
		items.add(new TopTenItem(1000005L, 9001009L, pf.getById(9001009L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand"));
		items.add(new TopTenItem(1000006L, 9002006L, pf.getById(9002006L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia"));
		items.add(new TopTenItem(1000007L, 9001019L, pf.getById(9001019L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit,", null, 1L, 1L, true, "http://google.com", "New Zealand"));
		items.add(new TopTenItem(1000008L, 9002004L, pf.getById(9002004L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia"));
		items.add(new TopTenItem(1000009L, 9001001L, pf.getById(9001001L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand"));
		} else if (list.getId().equals(1001L)) {
			items.add(new TopTenItem(1000000L, 9002007L, pf.getById(9002007L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia"));
			items.add(new TopTenItem(1000001L, 9001018L, pf.getById(9001018L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand"));
			items.add(new TopTenItem(1000002L, 9002006L, pf.getById(9002006L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia"));
			items.add(new TopTenItem(1000003L, 9001002L, pf.getById(9001002L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand"));
			items.add(new TopTenItem(1000004L, 9002017L, pf.getById(9002017L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia"));
			items.add(new TopTenItem(1000005L, 9001009L, pf.getById(9001009L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand"));
			items.add(new TopTenItem(1000006L, 9002001L, pf.getById(9002001L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia"));
			items.add(new TopTenItem(1000007L, 9001019L, pf.getById(9001019L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit,", null, 1L, 1L, true, "http://google.com","New Zealand"));
			items.add(new TopTenItem(1000008L, 9002004L, pf.getById(9002004L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia"));
			items.add(new TopTenItem(1000009L, 9001001L, pf.getById(9001001L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "New Zealand"));
		} else {
			items.add(new TopTenItem(1000000L, 9002007L, pf.getById(9002007L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, false, "http://google.com", "Australia"));
			items.add(new TopTenItem(1000001L, 9001018L, pf.getById(9001018L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com","New Zealand"));
			items.add(new TopTenItem(1000002L, 9002006L, pf.getById(9002006L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia"));
			items.add(new TopTenItem(1000003L, 9001002L, pf.getById(9001002L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com","New Zealand"));
			items.add(new TopTenItem(1000004L, 9002017L, pf.getById(9002017L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia"));
			items.add(new TopTenItem(1000005L, 9001009L, pf.getById(9001009L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com","New Zealand"));
			items.add(new TopTenItem(1000006L, 9002001L, pf.getById(9002001L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", null, 1L, 1L, false, "http://google.com", "Australia"));
			items.add(new TopTenItem(1000007L, 9001019L, pf.getById(9001019L), "", null, 1L, 1L, false, "http://google.com", "New Zealand"));
			items.add(new TopTenItem(1000008L, 9002004L, pf.getById(9002004L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com", "Australia"));
			items.add(new TopTenItem(1000009L, 9001001L, pf.getById(9001001L), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", null, 1L, 1L, true, "http://google.com","New Zealand"));
			}
		return items;
	}

	public List<ITopTenListSummary> getSummariesForComp(Long compId) {
		
		return null;
		
	}

	@Override
	public ITopTenItem put(ITopTenItem item) {
		return item;
	}


	@Override
	public ITopTenList put(ITopTenList list) {
		return list;
		
	}

	@Override
	public ITopTenItem submit(ITopTenItem item) {
		item.setSubmitted(!item.isSubmitted());
		return item;
	}

	@Override
	public ITopTenList getLatestForComp(Long compId) {
		if (compId.equals(1L)) return get(1001L);
		else return null;
		
	}
	
}
