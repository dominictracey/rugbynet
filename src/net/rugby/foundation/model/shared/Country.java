package net.rugby.foundation.model.shared;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class Country implements Serializable, ICountry {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1985625130474602398L;
	private String name;
	private String irb;
	private String abbr;
	private String nickName;
	@Id
	private Long id;

	public Country() {
		
	}
	
	public Country(Long id, String name, String irb, String abbr, String nickName) {
		this.id = id;
		this.name = name;
		this.irb = irb;
		this.abbr = abbr;
		this.nickName = nickName;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICountry#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICountry#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICountry#getIrb()
	 */
	@Override
	public String getIrb() {
		return irb;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICountry#setIrb(java.lang.String)
	 */
	@Override
	public void setIrb(String irb) {
		this.irb = irb;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICountry#getAbbr()
	 */
	@Override
	public String getAbbr() {
		return abbr;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICountry#setAbbr(java.lang.String)
	 */
	@Override
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICountry#getNickName()
	 */
	@Override
	public String getNickName() {
		return nickName;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICountry#setNickName(java.lang.String)
	 */
	@Override
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICountry#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICountry#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	
}
