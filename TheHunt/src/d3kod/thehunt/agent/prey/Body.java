package d3kod.thehunt.agent.prey;


public class Body extends BodyPart {
	public float bodyStartAngle;
	public float bodyBAngle;
	public float bodyCAngle;
	public float bodyEndAngle;
	
	protected float bodyStartAngleRot;
	protected float bodyBAngleRot;
	protected float bodyCAngleRot;
	protected float bodyEndAngleRot;
	
	public int bodyEndAngleTarget;
	public int bodyCAngleTarget;
	public int bodyBAngleTarget;
	public int bodyStartAngleTarget;
	
	public float rotateSpeedHead;
	public float bodyBSpeed;
	public float bodyCSpeed;
	public float bodyEndSpeed;
	private int bodyBendCounter;
	private boolean flopBack;
	protected int flopBackTargetFirst;
	protected int flopBackTargetSecond;
	protected boolean floppedFirst;
	protected boolean floppedSecond;
	protected boolean turningBackFinMotion;
	protected float flopBackAngle;
	
	private float flopBackSpeed;
	protected int backFinAngle;
	private boolean floppedThird;
	
	protected TurnAngle turningBackFinAngle;
	
	protected final float MAX_BODY_BEND_ANGLE = 110;
	private float mForce;
	
	public Body() {
		rotateSpeedHead = PreyData.rotateSpeedSmall;//Math.abs(TurnAngle.LEFT_SMALL.getValue())/SMALL_TICKS_PER_TURN;
	}
	
	@Override
	public BodyPartGraphic getGraphic(D3Prey graphic, float size) {
		return new BodyGraphic(graphic, this, size);
	}

	public float getTopAngle() {
		return bodyStartAngle;
	}

	public void update() {
		mForce = 0;
		if (bodyBendCounter == 0) {
			bodyEndAngleTarget = bodyCAngleTarget;
			bodyEndSpeed = bodyCSpeed;
			bodyCAngleTarget = bodyBAngleTarget;
			bodyCSpeed = bodyBSpeed;
			bodyBAngleTarget = bodyStartAngleTarget;
			bodyBSpeed = rotateSpeedHead;
			bodyBendCounter = PreyData.BODY_BEND_DELAY-1;
		}
		else {
			--bodyBendCounter;
		}

		if (bodyStartAngleTarget > bodyStartAngle + rotateSpeedHead) bodyStartAngleRot = rotateSpeedHead;
		else if (bodyStartAngleTarget < bodyStartAngle - rotateSpeedHead) bodyStartAngleRot = -rotateSpeedHead;
		else {
			bodyStartAngleRot = 0;
			bodyStartAngle = bodyStartAngleTarget;
		}

		if (bodyBAngleTarget > bodyBAngle + bodyBSpeed) bodyBAngleRot = +bodyBSpeed;
		else if (bodyBAngleTarget < bodyBAngle - bodyBSpeed) bodyBAngleRot = -bodyBSpeed;
		else {
			bodyBAngleRot = 0;
			bodyBAngle = bodyBAngleTarget;
		}

		if (bodyCAngleTarget > bodyCAngle + bodyCSpeed) bodyCAngleRot = +bodyCSpeed;
		else if (bodyCAngleTarget < bodyCAngle - bodyCSpeed) bodyCAngleRot = -bodyCSpeed;
		else {
			bodyCAngleRot = 0;
			bodyCAngle = bodyCAngleTarget;
		}	
		
		if (!flopBack) {
			if (bodyEndAngleTarget > bodyEndAngle + bodyEndSpeed) bodyEndAngleRot = bodyEndSpeed;
			else if (bodyEndAngleTarget < bodyEndAngle - bodyEndSpeed) bodyEndAngleRot = -bodyEndSpeed;
			else {
				bodyEndAngleRot = 0;
				bodyEndAngle = bodyEndAngleTarget;
			}
		}
		
		if (flopBack) doFlopBack();

		bodyStartAngle += bodyStartAngleRot;
		bodyBAngle += bodyBAngleRot;
		bodyCAngle += bodyCAngleRot;
		if (!flopBack) {
			bodyEndAngle += bodyEndAngleRot;
		}
	}
	
	private boolean stoppedTurning() {
		return (bodyStartAngleTarget == bodyBAngleTarget 
				&& bodyBAngleTarget == bodyCAngleTarget);
	}

	private void doFlopBack() {
		if (!floppedFirst) {
			if (flopBackTargetFirst > flopBackAngle + flopBackSpeed) flopBackAngle += flopBackSpeed;
			else if (flopBackTargetFirst < flopBackAngle - flopBackSpeed) flopBackAngle -= flopBackSpeed;
			else {
				flopBackAngle = flopBackTargetFirst;
				floppedFirst = true;
//				moveForward(Math.abs(backFinAngle*flopBackSpeed)); // F = ma
				mForce += Math.abs(backFinAngle*flopBackSpeed);
//				Log.v(TAG, "Flop back!");
//				putFlopText(flopBackAngle + bodyCAngle);
			}
			bodyEndAngleRot = bodyCAngle + flopBackAngle-bodyEndAngle;
			bodyEndAngle = bodyCAngle + flopBackAngle;
		}
		else if (!floppedSecond) {
			if (flopBackTargetSecond > flopBackAngle + flopBackSpeed) flopBackAngle += flopBackSpeed;
			else if (flopBackTargetSecond < flopBackAngle - flopBackSpeed) flopBackAngle -= flopBackSpeed;
			else {
				flopBackAngle = flopBackTargetSecond;
				floppedSecond = true;
//				moveForward(Math.abs(2*backFinAngle*flopBackSpeed)); // F = ma
				mForce += Math.abs(2*backFinAngle*flopBackSpeed);
//				Log.v(TAG, "Flop back!");
//				putFlopText(flopBackAngle + bodyCAngle);
			}
			bodyEndAngleRot = bodyCAngle + flopBackAngle-bodyEndAngle;
			bodyEndAngle = bodyCAngle + flopBackAngle;
		}
		else {
			//flopping third
			if (0 > flopBackAngle + flopBackSpeed) flopBackAngle += flopBackSpeed;
			else if (0 < flopBackAngle - flopBackSpeed) flopBackAngle -= flopBackSpeed;
			else {
				flopBackAngle = 0;
				floppedThird = true;
//				moveForward(Math.abs(backFinAngle*flopBackSpeed)); // F = ma
				mForce += Math.abs(backFinAngle*flopBackSpeed);
			}
			bodyEndAngleRot = bodyCAngle + flopBackAngle-bodyEndAngle;
			bodyEndAngle = bodyCAngle + flopBackAngle;
		}
		if (floppedFirst && floppedSecond && floppedThird) {
			if (turningBackFinMotion) {
				turningBackFinMotion = false;
				flopBack = false;
			}
			else {
				flopBack = false;
			}
		}
	}
	public void backFinMotion(TurnAngle angle) {
		flopBack = true;
		backFinAngle = angle.getValue();
		bodyEndAngle = bodyCAngle;
		flopBackTargetFirst = +backFinAngle;
		flopBackAngle = 0;
		flopBackTargetSecond = -backFinAngle;
		floppedFirst = false;
		floppedSecond = false;
		floppedThird = false;
		flopBackSpeed = angle.getRotateSpeed();
	}

	public boolean bend(TurnAngle angle) {
		int value = angle.getValue();
		
		if (bodyStartAngleTarget + value - bodyCAngle > MAX_BODY_BEND_ANGLE 
				|| bodyStartAngleTarget + value - bodyCAngle < -MAX_BODY_BEND_ANGLE) {
			return false;
		}
		rotateSpeedHead = angle.getRotateSpeed();
		bodyStartAngleTarget += value;

		if (!turningBackFinMotion) {
			turningBackFinMotion = true;
			turningBackFinAngle = angle.getBackAngle();
			backFinMotion(turningBackFinAngle);
		}
		return false;
	}

	public float getForce() {
		return mForce;
	}

	public float getBottomAngle() {
		return bodyEndAngle;
	}

	public double getFacingAngle() {
		return bodyCAngle;
	}

	public void noRot() {
		bodyEndAngleRot = bodyStartAngleRot = bodyBAngleRot = bodyCAngleRot = 0;
	}

}
