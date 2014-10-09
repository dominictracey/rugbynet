package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class Round implements Serializable, IRound {
	
	private static final long serialVersionUID = 1L;
	private Integer ordinal;
	@Id
	private Long id;
	private ArrayList<Long> matchIds = new ArrayList<Long>();
	@Transient
	private ArrayList<IMatchGroup> matches = new ArrayList<IMatchGroup>();
	private Date begin;
	private Date end;
	private Date computed;
	private String name;
	private String abbr;
	private Long compId;
	protected WorkflowStatus workflowStatus;
	
	public Round() {
		
	}
	
	
	public Round(Long id, ArrayList<IMatchGroup> matches, ArrayList<Long> matchIds, Date begin, Date end,
			Date computed) {
		super();
		this.id = id;
		this.matches = matches;
		this.matchIds = matchIds;
		this.begin = begin;
		this.end = end;
		this.computed = computed;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		
		if (o != null) {
			if (o instanceof Round) {
				Round r= (Round)o;
				
				// first of all the proposed round must have all the matches as in this round
				for (IMatchGroup m : matches) {
					boolean found = false;
					for (IMatchGroup om : r.getMatches()) {
						if (m.equals(om)) {
							found = true;
							break;
						}
					}
					if (found == false) {
						return false;
					}
				}
				
				// if that checks out, compare some attributes
				return (r.getName().equals(getName()) &&
						r.getBegin().equals(getBegin()) &&
						r.getEnd().equals(getEnd()));
			}
		}
		
		return false;
	}

	public void addMatchID(Long m) {
		matchIds.add(m);
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#getMatches()
	 */
	@Override
	public ArrayList<Long> getMatchIDs() {
		return matchIds;
	}
	public void setMatchIDs(ArrayList<Long> matchIds) {
		this.matchIds = matchIds;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#getBegin()
	 */
	@Override
	public Date getBegin() {
		return begin;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#setBegin(java.util.Date)
	 */
	@Override
	public void setBegin(Date begin) {
		this.begin = begin;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#getEnd()
	 */
	@Override
	public Date getEnd() {
		return end;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#setEnd(java.util.Date)
	 */
	@Override
	public void setEnd(Date end) {
		this.end = end;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#getComputed()
	 */
	@Override
	public Date getComputed() {
		return computed;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#setComputed(java.util.Date)
	 */
	@Override
	public void setComputed(Date computed) {
		this.computed = computed;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#getName()
	 */
	@Override
	public String getName() {
		return name;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#getAbbr()
	 */
	@Override
	public String getAbbr() {
		return abbr;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#setAbbr(java.lang.String)
	 */
	@Override
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#setOrdinal(java.lang.Integer)
	 */
	@Override
	public void setOrdinal(Integer e) {
		ordinal = e;
		
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#getOrdinal()
	 */
	@Override
	public Integer getOrdinal() {
		return ordinal;
		
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#setMatches(java.util.ArrayList)
	 */
	@Override
	public void setMatches(ArrayList<IMatchGroup> ids) {
		this.matches = ids;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#getMatches()
	 */
	@Override
	public ArrayList<IMatchGroup> getMatches() {
		return matches;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#addMatch(net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public void addMatch(IMatchGroup mg) {
		matches.add(mg);
		
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#getCompId()
	 */
	@Override
	public Long getCompId() {
		return compId;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRound#setCompId()
	 */
	@Override
	public void setCompId(Long compId) {
		this.compId = compId;
	}


	@Override
	public WorkflowStatus getWorkflowStatus() {
		return workflowStatus;
	}


	@Override
	public void setWorkflowStatus(WorkflowStatus status) {
		this.workflowStatus = status;
	}

}
