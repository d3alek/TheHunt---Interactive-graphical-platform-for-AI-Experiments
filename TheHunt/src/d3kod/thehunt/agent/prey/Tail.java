package d3kod.thehunt.agent.prey;

public class Tail extends BodyPart {

	@Override
	public BodyPartGraphic getGraphic(D3Prey graphic, float size) {
		return new TailGraphic(graphic, size);
	}

}
