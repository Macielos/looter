package pl.looter.appengine.domain;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import lombok.Data;

@Entity
@Data
public class Point {

	@Id
	private Long id;

	private Long eventId;
	
	private double x;
	
	private double y;

}
