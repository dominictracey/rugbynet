package net.rugby.foundation.topten.server.rest;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.Transient;

import net.rugby.foundation.model.shared.IHasId;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;

@Entity
public class RoundNode implements Serializable, JsonSerializable, IHasId  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1233688696919684387L;
	
	@Entity
	public static class PlayerMatch implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3455969458893817777L;
		
		public PlayerMatch() {
			
		}
		
		@Id
		protected Long id;
		@Indexed
		public Long playerId;
		
		@Unindexed
		public Integer rating;
		@Unindexed
		public Integer ranking;
		@Unindexed
		public String label;
		@Unindexed
		public String homeTeamAbbr;
		@Unindexed
		public String visitingTeamAbbr;
		@Unindexed
		public String homeTeamScore;
		@Unindexed
		public String visitingTeamScore;
		@Unindexed
		public String matchDate;
		@Unindexed
		public String position;
		@Unindexed
		public String matchRating;
		@Unindexed
		public String minutesPlayed;
		
//		public Map<String, String> notes = new HashMap<String, String>();
		@Unindexed
		public String notes;
		@Unindexed
		public String teamAbbr;

		public final static String sep = "|";

		public void setId(Long id) {
			this.id = id;
			
		}

		public Long getId() {
			return id;
		}

		
	}

	
	@Id
	protected Long id;
	@Indexed
	public Long compId;
	@Indexed
	public Integer positionOrdinal;
	@Indexed
	public Integer round;
	@Unindexed
	public String label;
	
	@Embedded
	protected List<PlayerMatch> _playerMatches = new ArrayList<PlayerMatch>();
	
	// Need the value to be a list of PMs because of the RWC where we have a player in two matches per round
	@Transient
	public Map<String, List<PlayerMatch>> playerMatches = new HashMap<String, List<PlayerMatch>>(); // key: player.displayName
	
	public RoundNode() {
		
	}
	

	 Map<String, List<PlayerMatch>> getPlayerMatches() {
		 return playerMatches;
	 }
	
	@Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof RoundNode)
        {
            sameSame = this.round == ((RoundNode) object).round;
        }

        return sameSame;
    }


	@Override
	public void serializeWithType(JsonGenerator arg0, SerializerProvider arg1,
			TypeSerializer arg2) throws IOException {
		arg0.writeObject(playerMatches);
		
	}


	@Override
	public void serialize(JsonGenerator arg0, SerializerProvider arg1)
			throws IOException {
		arg0.writeObject(playerMatches);
		
	}


	@Override
	public void setId(Long id) {
		this.id = id;
	}


	@Override
	public Long getId() {
		return id;
	}

//
//	public List<Long> getPlayerMatchIds() {
//		return playerMatchIds;
//	}
//
//
//	public void setPlayerMatchIds(List<Long> playerMatchIds) {
//		this.playerMatchIds = playerMatchIds;
//	}


	public Integer getPositionOrdinal() {
		return positionOrdinal;
	}


	public void setPositionOrdinal(Integer positionOrdinal) {
		this.positionOrdinal = positionOrdinal;
	}


	public List<PlayerMatch> get_playerMatches() {
		return _playerMatches;
	}


	public void set_playerMatches(List<PlayerMatch> playerMatches) {
		this._playerMatches = _playerMatches;
	}
}
