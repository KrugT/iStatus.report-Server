package de.helfenkannjeder.istatus.server.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MemberState {
	public enum State {
		AVAILABLE,
		UNAVAILABLE
	}

	private Member member;

	private State state;
	
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "MemberState [member=" + member + ", state=" + state + "]";
	}
}
