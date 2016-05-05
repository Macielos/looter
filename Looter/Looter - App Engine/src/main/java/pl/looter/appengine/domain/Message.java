package pl.looter.appengine.domain;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import lombok.Data;

/**
 * Created by Arjan on 05.05.2016.
 */
@Entity
@Data
public class Message {

	@Id
	Long id;

	String message;

	public Message(String message) {
		this.message = message;
	}
}
