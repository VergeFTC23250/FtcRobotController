
package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "StarterBotAuto", group = "StarterBot")
//@Disabled
public class StarterBotAuto extends OpMode {

    // Launcher settings
    public final int LAUNCHER_TARGET_VELOCITY = 1350;
    public final int LAUNCHER_MIN_VELOCITY = 1250;

    // Drive settings
    final double DRIVE_SPEED = 0.5;
    final double ROTATE_SPEED = 0.2;

    final double WHEEL_DIAMETER_MM = 96;
    final double ENCODER_TICKS_PER_REV = 537.7;

    final double TICKS_PER_MM =
            ENCODER_TICKS_PER_REV / (WHEEL_DIAMETER_MM * Math.PI);

    final double TRACK_WIDTH_MM = 402;

    // Individual drive distances (millimeters)
    final double DRIVE1_DISTANCE = -2260.6;
    final double DRIVE2_DISTANCE = -1371.6;
    final double DRIVE3_DISTANCE = 0;
    final double DRIVE4_DISTANCE = 0;

    // Autonomous timing
    final double LAUNCH_DURATION = 8.0;
    final double AUTO_TIMEOUT = 29.5;
    final double HOLD_SECONDS = 0.2;

    // Motor tolerance
    final double TOLERANCE_MM = 10;

    // Intake power
    double intakePower = 0;

    // Timers
    private final ElapsedTime driveTimer = new ElapsedTime();
    private final ElapsedTime autoTimer = new ElapsedTime();

    // Hardware
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;
    private DcMotorEx launcher = null;
    private DcMotor intake = null;

    private CRServo leftIntakeServo = null;
    private CRServo rightIntakeServo = null;
    private CRServo windmillServo = null;

    // Autonomous states
    private enum AutonomousState {
        LAUNCH,
        DRIVE1,
        TURN_LEFT1,
        DRIVE2,
        TURN_RIGHT,
        DRIVE3,
        TURN_LEFT2,
        DRIVE4,
        PARK,
        COMPLETE
    }

    private AutonomousState autonomousState;

    // Movement tracking
    private boolean movementInitialized = false;

    private int movementLeftTarget;
    private int movementRightTarget;

    @Override
    public void init() {

        autonomousState = AutonomousState.LAUNCH;

        // Initialize hardware
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        intake = hardwareMap.get(DcMotor.class, "intake");
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");

        windmillServo = hardwareMap.get(CRServo.class, "windmill");

        leftIntakeServo =
                hardwareMap.get(CRServo.class, "left_intake_servo");

        rightIntakeServo =
                hardwareMap.get(CRServo.class, "right_intake_servo");

        // Motor directions
        leftDrive.setDirection(DcMotor.Direction.FORWARD);
        rightDrive.setDirection(DcMotor.Direction.REVERSE);
        intake.setDirection(DcMotorSimple.Direction.REVERSE);

        // Reset drive encoders
        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Brake behavior
        leftDrive.setZeroPowerBehavior(BRAKE);
        rightDrive.setZeroPowerBehavior(BRAKE);
        intake.setZeroPowerBehavior(BRAKE);

        // Launcher setup
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        launcher.setPIDFCoefficients(
                DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(40, 0, 0, 12.5)
        );

        // Initialize servos
        leftIntakeServo.setPower(0);
        rightIntakeServo.setPower(0);
        windmillServo.setPower(0);

        rightIntakeServo.setDirection(DcMotorSimple.Direction.REVERSE);
        windmillServo.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void start() {
        autoTimer.reset();
    }

    @Override
    public void loop() {

        // Stop the autonomous routine before the 30-second limit.
        if (autoTimer.seconds() >= AUTO_TIMEOUT) {
            stopAllMotors();
            autonomousState = AutonomousState.COMPLETE;
        }

        switch (autonomousState) {

            case LAUNCH:

                launch(true);

                if (autoTimer.seconds() >= LAUNCH_DURATION) {
                    launch(false);
                    intakePower = 0;
                    autonomousState = AutonomousState.DRIVE1;
                }

                break;

            case DRIVE1:

                if (drive(DRIVE1_DISTANCE)) {
                    autonomousState = AutonomousState.TURN_LEFT1;
                }

                break;

            case TURN_LEFT1:

                if (rotate(100)) {
                    autonomousState = AutonomousState.DRIVE2;
                }

                break;

            case DRIVE2:

                if (drive(DRIVE2_DISTANCE)) {
                    autonomousState = AutonomousState.TURN_RIGHT;
                }

                break;

            case TURN_RIGHT:

                if (rotate(0)) {
                    autonomousState = AutonomousState.DRIVE3;
                }

                break;

            case DRIVE3:

                if (drive(DRIVE3_DISTANCE)) {
                    autonomousState = AutonomousState.TURN_LEFT2;
                }

                break;

            case TURN_LEFT2:

                if (rotate(0)) {
                    autonomousState = AutonomousState.DRIVE4;
                }

                break;

            case DRIVE4:

                if (drive(DRIVE4_DISTANCE)) {
                    autonomousState = AutonomousState.PARK;
                }

                break;

            case PARK:

                stopAllMotors();
                autonomousState = AutonomousState.COMPLETE;

                break;

            case COMPLETE:

                stopAllMotors();
                telemetry.addLine("Auto Complete!");

                break;
        }

        // Update intake power
        intake.setPower(intakePower);

        // Telemetry
        telemetry.addData("AutoState", autonomousState);

        telemetry.addData(
                "Time",
                "%.1f seconds",
                autoTimer.seconds()
        );

        telemetry.addData(
                "Left Position",
                leftDrive.getCurrentPosition()
        );

        telemetry.addData(
                "Right Position",
                rightDrive.getCurrentPosition()
        );

        telemetry.addData(
                "Left Target",
                leftDrive.getTargetPosition()
        );

        telemetry.addData(
                "Right Target",
                rightDrive.getTargetPosition()
        );

        telemetry.update();
    }

    @Override
    public void stop() {
        stopAllMotors();
    }

    // Launcher control
    void launch(boolean input) {

        if (input) {
            launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
        } else {
            launcher.setVelocity(0);
        }

        if (input &&
                launcher.getVelocity() > LAUNCHER_MIN_VELOCITY) {

            windmillServo.setPower(1);
            intakePower = 1.0;

        } else {

            windmillServo.setPower(0);
        }
    }

    // Drive forward by the requested distance (millimeters).
    boolean drive(double distance) {

        if (!movementInitialized) {

            int movementTicks =
                    (int) (distance * TICKS_PER_MM);

            movementLeftTarget =
                    leftDrive.getCurrentPosition() + movementTicks;

            movementRightTarget =
                    rightDrive.getCurrentPosition() + movementTicks;

            leftDrive.setTargetPosition(movementLeftTarget);
            rightDrive.setTargetPosition(movementRightTarget);

            leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            leftDrive.setPower(DRIVE_SPEED);
            rightDrive.setPower(DRIVE_SPEED);

            driveTimer.reset();
            movementInitialized = true;
        }

        boolean leftAtTarget =
                Math.abs(movementLeftTarget -
                        leftDrive.getCurrentPosition())
                        <= TOLERANCE_MM * TICKS_PER_MM;

        boolean rightAtTarget =
                Math.abs(movementRightTarget -
                        rightDrive.getCurrentPosition())
                        <= TOLERANCE_MM * TICKS_PER_MM;

        if (!leftAtTarget || !rightAtTarget) {
            driveTimer.reset();
            return false;
        }

        if (driveTimer.seconds() >= HOLD_SECONDS) {

            leftDrive.setPower(0);
            rightDrive.setPower(0);

            movementInitialized = false;

            return true;
        }

        return false;
    }

    // Rotate the robot by the requested angle.
    // Positive angle = left turn.
    // Negative angle = right turn.
    boolean rotate(double angle) {

        if (!movementInitialized) {

            double targetMm =
                    Math.toRadians(angle) * (TRACK_WIDTH_MM / 2);

            int leftMovementTicks =
                    (int) (-targetMm * TICKS_PER_MM);

            int rightMovementTicks =
                    (int) (targetMm * TICKS_PER_MM);

            movementLeftTarget =
                    leftDrive.getCurrentPosition() + leftMovementTicks;

            movementRightTarget =
                    rightDrive.getCurrentPosition() + rightMovementTicks;

            leftDrive.setTargetPosition(movementLeftTarget);
            rightDrive.setTargetPosition(movementRightTarget);

            leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            leftDrive.setPower(ROTATE_SPEED);
            rightDrive.setPower(ROTATE_SPEED);

            driveTimer.reset();
            movementInitialized = true;
        }

        boolean leftAtTarget =
                Math.abs(movementLeftTarget -
                        leftDrive.getCurrentPosition())
                        <= TOLERANCE_MM * TICKS_PER_MM;

        boolean rightAtTarget =
                Math.abs(movementRightTarget -
                        rightDrive.getCurrentPosition())
                        <= TOLERANCE_MM * TICKS_PER_MM;

        if (!leftAtTarget || !rightAtTarget) {
            driveTimer.reset();
            return false;
        }

        if (driveTimer.seconds() >= HOLD_SECONDS) {

            leftDrive.setPower(0);
            rightDrive.setPower(0);

            movementInitialized = false;

            return true;
        }

        return false;
    }

    // Stop all motors and servos.
    void stopAllMotors() {

        if (leftDrive != null) {
            leftDrive.setPower(0);
        }

        if (rightDrive != null) {
            rightDrive.setPower(0);
        }

        if (intake != null) {
            intake.setPower(0);
        }

        if (launcher != null) {
            launcher.setVelocity(0);
        }

        if (windmillServo != null) {
            windmillServo.setPower(0);
        }

        if (leftIntakeServo != null) {
            leftIntakeServo.setPower(0);
        }

        if (rightIntakeServo != null) {
            rightIntakeServo.setPower(0);
        }
    }
}
