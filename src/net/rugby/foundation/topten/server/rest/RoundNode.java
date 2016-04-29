package net.rugby.foundation.topten.server.rest;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;


public class RoundNode implements Serializable, JsonSerializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1233688696919684387L;
	public Integer round;
	public String label;
	public Map<String, PlayerMatch> playerMatches = new HashMap<String, PlayerMatch>();
	
	public RoundNode() {
		
	}
	

	 Map<String, PlayerMatch> getPlayerMatches() {
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
}
