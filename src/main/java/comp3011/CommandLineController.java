/*
 * Starter code supplied for Adelaide University COMP3011 Assignment 2.
 * Students are free to modify this file for assessment purposes.
 * 
 * Authors:
 *   1. Simon Ratcliffe, in collaboration with GPT-5.6 Terra
 *   2. <student name and student number insert here upon modification>
 *
 * Copyright 2026 Simon Ratcliffe
 */
package comp3011;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created in the VideoPlayerApp's main function to receive and parse the video
 * player's command-line arguments.
 *
 * <p>
 * Extracts the video file and supported launch options, reports invalid input
 * or help text, and exposes the resulting launch configuration via methods to
 * the {@link VideoPlayerApp}.
 * </p>
 */
public class CommandLineController {

	private final String[] args;

	// Settings on start up
	private boolean helpRequested;
	private boolean audioRequested;
	private boolean maximiseRequested;
	// new requirement
	private boolean subtitlesRequested;

	// Information about the video required for start up
	private Integer displayId;
	private Integer outputFrameNumber;
	private File videoFile;
	private String errorMessage;

	// list of frames
	private List<FrameProcessor> Frames;

	// Constructor for the command line to parse the input arguments
	public CommandLineController(String[] args) {
		this.args = args.clone();
		parse();
		if (errorMessage != null) {
			System.out.println(errorMessage);
		}
		if (helpRequested) {
			printHelp();
		}
	}

	public File getVideoFile() {
		return videoFile;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public int getExitCode() {
		return errorMessage == null ? 0 : 1;
	}

	public Integer getDisplayId() {
		return displayId;
	}

	public boolean isAudioRequested() {
		return audioRequested;
	}

	public boolean isMaximiseRequested() {
		return maximiseRequested;
	}

	public boolean isOutputFrameRequested() {
		return outputFrameNumber != null;
	}

	public boolean isSubtitlesRequested() {
		return subtitlesRequested;
	}

	public int getOutputFrameNumber() {
		return outputFrameNumber;
	}

	public boolean shouldLaunchApplication() {
		return errorMessage == null && videoFile != null;
	}

	// get the list of frame processors
	public List<FrameProcessor> getFrames() {
		return Frames;
	}

	private void parse() {
		List<String> videoFiles = new ArrayList<>();
		Frames = new ArrayList<>();
		for (String arg : args) {
			switch (arg) {
			case "--help", "-h":
				System.out.println("help");
				helpRequested = true;

			case "--audio", "-a":
				System.out.println("audio");
				audioRequested = true;
			
			case "--maxamise", "-x":
				System.out.println("maximise");
				maximiseRequested = true;
			
			case "--monitor-1", "-1":
				System.out.println("monitor-1");
				setDisplayId(1);
			
			case "--monitor-2", "-2":
				System.out.println("monitor-2");
				setDisplayId(2);
				
			case "":
				
			default:
				// something that falls outside of arguments
				errorMessage = "Unknown option" + arg;
			}
			if ("-h".equals(arg) || "--help".equals(arg)) {
				helpRequested = true;
			} else if ("-a".equals(arg) || "--audio".equals(arg)) {
				audioRequested = true;
			} else if ("-x".equals(arg) || "--maximise".equals(arg)) {
				maximiseRequested = true;
			} else if ("-o".equals(arg) || "--output-frame".equals(arg)) {
				argIndex = parseOutputFrameNumber(argIndex);
			} else if ("-1".equals(arg) || "--monitor-1".equals(arg)) {
				setDisplayId(1);
			} else if ("-2".equals(arg) || "--monitor-2".equals(arg)) {
				setDisplayId(2);

				//
			} else if ("--number-frames".equals(arg)) {
				frameProcessors.add(new FrameNumberer());
			} else if ("--scratch-frames".equals(arg)) {
				frameProcessors.add(new FrameScratcher());
			} else if ("--flicker-frames".equals(arg)) {
				frameProcessors.add(new FrameFlickerer());
			} else if ("--black-and-white".equals(arg)) {
				frameProcessors.add(new FrameBlackAndWhiter());
			} else if ("--yellow-frames".equals(arg)) {
				frameProcessors.add(new FrameYellower());
			} else if ("--vignette-frames".equals(arg)) {
				frameProcessors.add(new FrameVignetter());
			} else if ("--dust-frames".equals(arg)) {
				frameProcessors.add(new FrameDuster());
			} else if ("--jitter-frames".equals(arg)) {
				frameProcessors.add(new FrameJitterer());
			} else if ("--mottle-frames".equals(arg)) {
				frameProcessors.add(new FrameMottler());
			} else if ("--bleed-frames".equals(arg)) {
				frameProcessors.add(new FrameBleeder());
			} else if ("--pepper-frames".equals(arg)) {
				frameProcessors.add(new FramePepperer());
			} else if (isFrameProcessorCluster(arg)) {
				parseFrameProcessorCluster(arg);
			} else if (arg.startsWith("-")) {
				errorMessage = "Unknown option: " + arg;
			} else {
				videoFiles.add(arg);
			}
		}

		if (errorMessage != null) {
			return;
		}

		if (videoFiles.size() > 1) {
			errorMessage = "Usage: VideoPlayer [options] [video-file]";
		} else if (videoFiles.size() == 1) {
			videoFile = new File(videoFiles.get(0));
			if (!videoFile.isFile()) {
				errorMessage = "File not found: " + videoFile.getPath();
				videoFile = null;
			}
		} else {
			if (!helpRequested) {
				errorMessage = "No video file specified.";
			}
		}
	}

	private int parseOutputFrameNumber(int argIndex) {
		if (argIndex + 1 >= args.length) {
			errorMessage = "Missing frame number after " + args[argIndex];
			return argIndex;
		}

		String frameNumberText = args[argIndex + 1];
		try {
			int frameNumber = Integer.parseInt(frameNumberText);
			if (frameNumber <= 0) {
				errorMessage = "Output frame number must be positive: " + frameNumberText;
			} else if (outputFrameNumber != null) {
				errorMessage = "Only one output frame option can be used";
			} else {
				outputFrameNumber = frameNumber;
			}
		} catch (NumberFormatException e) {
			errorMessage = "Output frame number must be a positive integer: " + frameNumberText;
		}

		return argIndex + 1;
	}

	private void setDisplayId(int displayId) {
		if (this.displayId != null && this.displayId != displayId) {
			errorMessage = "Only one display option can be used";
			return;
		}
		this.displayId = displayId;
	}

	//
	private void printHelp() {
		System.out.println("Usage: VideoPlayer [options] [video-file]");
		System.out.println();
		System.out.println("Options:");
		System.out.println("  -h, --help         Show this help message");
		System.out.println("  -a, --audio        Play audio");
		System.out.println("  -x, --maximise     Open the player maximised");
		System.out.println("  -1, --monitor-1    Open the player on display 1");
		System.out.println("  -2, --monitor-2    Open the player on display 2");
		// System.out.println(" -o, --output-frame N Save a .png of a specific frame and
		// exit");
		System.out.println();
		System.out.println("Frame processors:");
		System.out.println("  -n, --number-frames    Render the frame number onto each frame");
		System.out.println("  -s, --scratch-frames   Render vertical film scratches");
		System.out.println("  -f, --flicker-frames   Randomly dim frames");
		System.out.println("  -w, --black-and-white  Convert frames to black and white");
		System.out.println("  -y, --yellow-frames    Apply a warmer colour temperature");
		System.out.println("  -v, --vignette-frames  Darken the frame edges");
		System.out.println("  -d, --dust-frames      Render dust and hair marks");
		System.out.println("  -j, --jitter-frames    Randomly displace frames by a few pixels");
		System.out.println("  -m, --mottle-frames    Add cloudy emulsion mottling");
		System.out.println("  -b, --bleed-frames     Bleed light into frames");
		System.out.println("  -p, --pepper-frames    Pepper frames with dark spots/blotches");
		System.out.println();
		System.out.println("Frame processors are applied in command-line order and may be repeated.");
		System.out.println("Example: -nssnfwyvdjmbp numbers, scratches twice, numbers again, flickers,");
		System.out.println("converts, warms, vignettes, dusts, jitters, mottles, bleeds, then peppers.");
	}
}
