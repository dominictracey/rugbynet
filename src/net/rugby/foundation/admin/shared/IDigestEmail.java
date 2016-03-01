package net.rugby.foundation.admin.shared;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.model.shared.IHasId;

import org.joda.time.DateTime;

public interface IDigestEmail extends IHasId {

	public abstract String getSubject();

	public abstract void setSubject(String subject);

	public abstract String getMessage();

	public abstract void setMessage(String message);

	public abstract DateTime getSent();

	public abstract void setSent(DateTime sent);

	public abstract List<IBlurb> getBlurbs();

	public abstract void setBlurbs(List<IBlurb> blurbs);

	List<Long> getBlurbIds();

	void setBlurbIds(List<Long> blurbIds);

	void setTitle(String title);

	String getTitle();

	public String getPart1();
	public void setPart1(String part1);
	public String getPart2();
	public void setPart2(String part2);
	public String getPart3();
	public void setPart3(String part3);
	public String getPart4();
	public void setPart4(String part4);
	public String getPart5();
	public void setPart5(String part5);
	public String getPart6();
	public void setPart6(String part6);
	public String getPart7();
	public void setPart7(String part7);

	Map<Long, String> getFormattedBlurbMap();
	void setFormattedBlurbMap(Map<Long, String> formattedBlurbMap);
	
}