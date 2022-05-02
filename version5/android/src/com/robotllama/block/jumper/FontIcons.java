package com.robotllama.block.jumper;

public enum FontIcons {

	REPLAY("r"),
	SOUND("o"),
	STAR("s"),
	HOME("h"),
	MUTE("m"),
	PLAY("p"),
	GOOGLE("g"),
	JUMP("g"),
	SHARE("c"),
	SCORES("1");
	
    private final String string;

    private FontIcons(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
	
}
