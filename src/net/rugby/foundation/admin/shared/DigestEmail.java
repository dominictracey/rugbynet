package net.rugby.foundation.admin.shared;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Transient;

import net.rugby.foundation.model.shared.IAppUser;

import org.joda.time.DateTime;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class DigestEmail implements IDigestEmail, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2959748434586070355L;
	@Id
	protected Long id;
	protected String subject;
	protected String message;
	private String title;
	protected DateTime sent;
	
	@Transient
	protected List<IBlurb> blurbs;
	protected List<Long> blurbIds;
	
	@Transient protected String part1;
	@Transient protected String part2;
	@Transient protected String part3;
	@Transient protected String part4;
	@Transient protected String part5;
	@Transient protected String part6;
	@Transient protected String part7;

	@Transient protected Map<Long, String> formattedBlurbMap = null;
	
	public DigestEmail() {
		
	}
	
	public DigestEmail(String subject, String message, DateTime sent) {
		super();
		this.subject = subject;
		this.message = message;
		this.sent = sent;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IDigestEmail#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IDigestEmail#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IDigestEmail#getSubject()
	 */
	@Override
	public String getSubject() {
		return subject;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IDigestEmail#setSubject(java.lang.String)
	 */
	@Override
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IDigestEmail#getMessage()
	 */
	@Override
	public String getMessage() {
		return message;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IDigestEmail#setMessage(java.lang.String)
	 */
	@Override
	public void setMessage(String message) {
		this.message = message;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IDigestEmail#getSent()
	 */
	@Override
	public DateTime getSent() {
		return sent;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IDigestEmail#setSent(org.joda.time.DateTime)
	 */
	@Override
	public void setSent(DateTime sent) {
		this.sent = sent;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IDigestEmail#getBlurbs()
	 */
	@Override
	public List<IBlurb> getBlurbs() {
		return blurbs;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IDigestEmail#setBlurbs(java.util.List)
	 */
	@Override
	public void setBlurbs(List<IBlurb> blurbs) {
		this.blurbs = blurbs;
	}
	@Override
	public List<Long> getBlurbIds() {
		return blurbIds;
	}
	@Override
	public void setBlurbIds(List<Long> blurbIds) {
		this.blurbIds = blurbIds;
	}
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String getPart1() {
		return part1;
	}
	@Override
	public void setPart1(String part1) {
		this.part1 = part1;
	}
	@Override
	public String getPart2() {
		return part2;
	}
	@Override
	public void setPart2(String part2) {
		this.part2 = part2;
	}
	@Override
	public String getPart3() {
		return part3;
	}
	@Override
	public void setPart3(String part3) {
		this.part3 = part3;
	}
	@Override
	public String getPart4() {
		return part4;
	}
	@Override
	public void setPart4(String part4) {
		this.part4 = part4;
	}
	@Override
	public String getPart5() {
		return part5;
	}
	@Override
	public void setPart5(String part5) {
		this.part5 = part5;
	}
	@Override
	public String getPart6() {
		return part6;
	}
	@Override
	public void setPart6(String part6) {
		this.part6 = part6;
	}
	@Override
	public String getPart7() {
		return part7;
	}
	@Override
	public void setPart7(String part7) {
		this.part7 = part7;
	}
	@Override
	public Map<Long, String> getFormattedBlurbMap() {
		return formattedBlurbMap;
	}
	@Override
	public void setFormattedBlurbMap(Map<Long, String> formattedBlurbMap) {
		this.formattedBlurbMap = formattedBlurbMap;
	}
	
}
