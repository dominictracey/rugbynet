package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;

import com.googlecode.objectify.annotation.Subclass;


@SuppressWarnings("serial")
@Subclass
public class PositionEnUs extends Position implements Serializable {
	
	
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
	private final static String[] enUSNames = new String[] {
		"--", "Prop", "Hooker", "Lock", "Flanker","Number 8", "Scrumhalf", "Flyhalf", "Wing", "Center", "Fullback" };
	
	@SuppressWarnings("unused")
	private final static String[] enUSAbbrsSimple = new String[] {
		"--", "PR", "HK",  "LK", "FL", "#8", "SH", "FH", "WG",  "CT", "WG", "FB" };

	private final static String[] enUSGroupingNames = new String[] { "ALL", "FORWARD", "BACK", "FRONTROW", "PROP", "BACKFIVE", "LOCK", "FLANKER", "BACKROW", "HALFBACK", "CENTER", "BACKTHREE", "WING" };
	

    public Long test;
    
	public PositionEnUs() {
		super();
	}
	
	public PositionEnUs(position p) {
		super(p);
	}

	public PositionEnUs(position p, ArrayList<position> l) {
		super(p,l);
	}

	@Override
	public String getName(position p) {
		
		switch (p) 	{

			case NONE :
			{
				return (enUSNames[0]);
			}

			case PROP :
			{
				return (enUSNames[1]);
			}
			
			case HOOKER :
			{
				return (enUSNames[2]);
			}
				
			case LOCK :
			{
				return (enUSNames[3]);
			}
	
			case FLANKER :
			{
				return (enUSNames[4]);
			}
			
			case NUMBER8 :
			{
				return (enUSNames[5]);
			}
			
			case SCRUMHALF :
			{
				return (enUSNames[6]);
			}
			
			case FLYHALF :
			{
				return (enUSNames[7]);
			}
	
			case WING :
			{
				return (enUSNames[8]);
			}
			
			case CENTER :
			{
				return (enUSNames[9]);
			}	
	
			case FULLBACK :
			{
				return (enUSNames[10]);
			}
		}
		
		return null;
	}


	@Override
	public String getName(grouping g) {
		
		switch (g) {
		
			case ALL : return (enUSGroupingNames[0]); 		
			case FORWARD : return (enUSGroupingNames[1]); 		
			case BACK : return (enUSGroupingNames[2]); 		
			case FRONTROW : return (enUSGroupingNames[3]); 		
			case PROP : return (enUSGroupingNames[4]); 		
			case BACKFIVE : return (enUSGroupingNames[5]); 		
			case LOCK : return (enUSGroupingNames[6]); 		
			case FLANKER : return (enUSGroupingNames[7]); 		
			case BACKROW : return (enUSGroupingNames[8]); 		
			case HALFBACK : return (enUSGroupingNames[9]); 		
			case CENTER : return (enUSGroupingNames[10]); 		
			case BACKTHREE : return (enUSGroupingNames[11]); 		
			case WING : return (enUSGroupingNames[12]); 		
		}

		return null;
	}	



}
	
	
