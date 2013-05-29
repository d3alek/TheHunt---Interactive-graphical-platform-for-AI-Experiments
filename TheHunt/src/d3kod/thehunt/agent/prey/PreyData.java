package d3kod.thehunt.agent.prey;

import android.graphics.PointF;
import d3kod.thehunt.world.floating_text.PanicText;
import d3kod.thehunt.world.logic.TheHuntRenderer;

public class PreyData {
	
	public static boolean AI = true;
	public static final int BODY_BENDS_PER_SECOND_MAX = 30;
	public static int BODY_BENDS_PER_SECOND = 12;
	public static int BODY_BEND_DELAY = TheHuntRenderer.TICKS_PER_SECOND/BODY_BENDS_PER_SECOND;
	public static final int ACTIONS_PER_SECOND_MAX = 30;
	private static final int EAT_PER_SECOND = 2;
	public static int ACTIONS_PER_SECOND = 30;
	public static int ACTION_DELAY = TheHuntRenderer.TICKS_PER_SECOND/ACTIONS_PER_SECOND;
	
	protected final float MAX_BODY_BEND_ANGLE = 110;
	
	private static int SMALL_TURNS_PER_SECOND = 3;
	private static int MEDIUM_TURNS_PER_SECOND = 4;
	private static int LARGE_TURNS_PER_SECOND = 3;
	private static int SMALL_TURNS_BACK_PER_SECOND = 2;
	private static int MEDIUM_TURNS_BACK_PER_SECOND = 3;
	private static int LARGE_TURNS_BACK_PER_SECOND = 2;
	public static int SMALL_TICKS_PER_TURN = TheHuntRenderer.TICKS_PER_SECOND/SMALL_TURNS_PER_SECOND;
	public static int MEDIUM_TICKS_PER_TURN = TheHuntRenderer.TICKS_PER_SECOND/MEDIUM_TURNS_PER_SECOND;
	public static int LARGE_TICKS_PER_TURN = TheHuntRenderer.TICKS_PER_SECOND/LARGE_TURNS_PER_SECOND;
	public static int SMALL_TICKS_BACK_PER_TURN = TheHuntRenderer.TICKS_PER_SECOND/SMALL_TURNS_BACK_PER_SECOND;
	public static int MEDIUM_TICKS_BACK_PER_TURN = TheHuntRenderer.TICKS_PER_SECOND/MEDIUM_TURNS_BACK_PER_SECOND;
	public static int LARGE_TICKS_BACK_PER_TURN = TheHuntRenderer.TICKS_PER_SECOND/LARGE_TURNS_BACK_PER_SECOND;
	public static int EAT_TICKS = TheHuntRenderer.TICKS_PER_SECOND/EAT_PER_SECOND;
	
	public static int angleFlopSmall = 20;
	public static int angleFlopMedium = 45;
	public static int angleFlopLarge = 90;
	public static float rotateSpeedSmall = angleFlopSmall*SMALL_TURNS_PER_SECOND/(TheHuntRenderer.TICKS_PER_SECOND);
	public static float rotateSpeedMedium = angleFlopMedium*MEDIUM_TURNS_PER_SECOND/(TheHuntRenderer.TICKS_PER_SECOND);
	public static float rotateSpeedLarge = angleFlopLarge*LARGE_TURNS_PER_SECOND/(TheHuntRenderer.TICKS_PER_SECOND);
	public static int angleFlopBackSmall = 15;
	public static int angleFlopBackMedium = 20;
	public static int angleFlopBackLarge = 30;
	public static float rotateSpeedBackSmall = 4*angleFlopBackSmall/SMALL_TICKS_BACK_PER_TURN;
	public static float rotateSpeedBackMedium = 4*angleFlopBackMedium/MEDIUM_TICKS_BACK_PER_TURN;
	public static float rotateSpeedBackLarge = 4*angleFlopBackLarge/LARGE_TICKS_BACK_PER_TURN;
	
	protected final int maxAngle = 60;
	protected final int minAngle = 0;
	
	protected float vHeadLeft;
	protected float vHeadRight;
	protected float vTailLeft;
	protected float vTailRight;
	protected float forwardAngleSpeed;

	protected float vx;
	protected float vy;
	protected float mPosY;
	protected float mPosX;

	protected float mPosHeadX;
	protected float mPosHeadY;
	
	public float bodyStartAngle;
	public float bodyBAngle;
	public float bodyCAngle;
	public float bodyEndAngle;
	
	public int bodyEndAngleTarget;
	public int bodyCAngleTarget;
	public int bodyBAngleTarget;
	public int bodyStartAngleTarget;
	public float rotateSpeedHead;
	public float bodyBSpeed;
	public float bodyCSpeed;
	public float bodyEndSpeed;
	
	protected boolean mIsCaught;
	
	protected float bodyStartAngleRot;
	protected float bodyBAngleRot;
	protected float bodyCAngleRot;
	protected float bodyEndAngleRot;
	
	protected float[] mHeadPosMatrix = new float[16];
	protected float[] mTailPosMatrix = new float[16];
	protected int bodyBendCounter;
	protected int backFinAngle;
	
	protected boolean flopBack;
	protected int flopBackTargetFirst;
	protected int flopBackTargetSecond;
	protected boolean floppedFirst;
	protected boolean floppedSecond;
	protected boolean turningBackFinMotion;
	protected float flopBackAngle;
	protected TurnAngle turningBackFinAngle;
	
	transient protected PanicText emotionText;
	public PointF mPosTail;
}
