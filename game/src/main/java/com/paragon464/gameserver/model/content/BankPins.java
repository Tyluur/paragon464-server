package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.BankTransfer;

public class BankPins {

    static final String[] MESSAGES = {"Now click the SECOND digit.", "Time for the THIRD digit.", "Finally, the FOURTH digit.", "Finally, the FOURTH digit."};
    private static final int ENTER_PIN_INTERFACE = 13;
    private static final int PIN_SETTINGS_INTERFACE = 14;
    public boolean enteredPin = false;
    private Player player;
    private int nextIndex = 0;
    private boolean changingPin, changingPinCheck;
    private int[] attemptedPin = new int[4];
    private int[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    public BankPins(Player player) {
        this.player = player;
    }

    public void open() {
        openPinSettings();
    }

    private void openPinSettings() {
        player.getFrames().sendInterfaceVisibility(14, 220, false);
        player.getFrames().sendComponentPosition(14, 225, 3000, 3000);//yes, I really want to do that.
        player.getFrames().sendComponentPosition(14, 230, 3000, 3000);//No, forget I asked.
        player.getFrames().sendComponentPosition(14, 220, 3000, 3000);//Big black square with red bars
        player.getFrames().sendComponentPosition(14, 133, 3000, 3000);//delete your pin
        player.getFrames().sendComponentPosition(14, 132, 3000, 3000);//change your pin
        player.getFrames().sendComponentPosition(14, 134, 3000, 3000);//change your recov delay - top
        player.getFrames().sendComponentPosition(14, 135, 3000, 3000);//Cancel the pin that's pending
        player.getInterfaceSettings().openInterface(PIN_SETTINGS_INTERFACE);
    }

    public void handle(int interfaceId, int buttonId) {
        switch (interfaceId) {
            case PIN_SETTINGS_INTERFACE:
                switch (buttonId) {
                    case 130:// Set a pin
                        changingPin = true;
                        openEnterPin();
                        break;
                }
                break;
            case ENTER_PIN_INTERFACE:
                if (buttonId >= 100 && buttonId <= 110) {
                    int index = buttonId - 100;
                    if (index == 0 && buttonId == 109) {
                        index = 9;
                    }
                    int number = numbers[index];
                    if (nextIndex < 3) {
                        int child = 140 + nextIndex;
                        player.getFrames().modifyText("*", 13, child);
                        nextIndex++;
                        attemptedPin[nextIndex] = number;
                        String type = nextIndex == 1 ? "2nd" : nextIndex == 2 ? "3rd" : nextIndex == 3 ? "4th" : "";
                        player.getFrames().modifyText("Click the " + type + " digit...", 13, 151);
                    } else {
                        nextIndex = 0;
                        if (changingPin && !changingPinCheck) {
                            if (player.getAttributes().getInt("bank_pin_hash") == -1) {//player has no pin.
                                changingPinCheck = true;
                                player.getFrames().sendMessage("Please set your new PIN.");
                                openEnterPin();
                                return;
                            }
                            boolean match = true;
                            for (int i = 0; i < attemptedPin.length; i++) {
                                int pin = getPinForIndex(i);
                                if (attemptedPin[i] != pin) {
                                    match = false;
                                    break;
                                }
                            }
                            if (!match) {
                                player.getFrames().sendMessage("The pin you entered was incorrect.");
                                player.getInterfaceSettings().closeInterfaces(false);
                                return;
                            } else {
                                changingPinCheck = true;
                                player.getFrames().sendMessage("Please set your new PIN.");
                                openEnterPin();
                            }
                            return;
                        }
                        if (player.getAttributes().getInt("bank_pin_hash") == -1 || (changingPin && changingPinCheck)) {//we set a new pin
                            player.getAttributes().set("bank_pin_hash", attemptedPin[0] + (attemptedPin[1] << 8) + (attemptedPin[2] << 16) + (attemptedPin[3] << 24));
                            enteredPin = true;
                            changingPinCheck = false;
                            changingPin = false;
                            player.getFrames().sendMessage("Your pin is now set.");
                            player.getVariables().setTransferContainer(new BankTransfer(player));
                        } else {//we confirm with current pin
                            boolean match = true;
                            for (int i = 0; i < attemptedPin.length; i++) {
                                int pin = getPinForIndex(i);
                                if (attemptedPin[i] != pin) {
                                    match = false;
                                    break;
                                }
                            }
                            if (!match) {
                                player.getFrames().sendMessage("The pin you entered was incorrect.");
                                player.getInterfaceSettings().closeInterfaces(false);
                                return;
                            } else {
                                enteredPin = true;
                                player.getFrames().sendMessage("You successfully entered your bank pin.");
                                player.getVariables().setTransferContainer(new BankTransfer(player));
                            }
                        }
                    }
                } else if (buttonId == 149) {
                    player.getInterfaceSettings().closeInterfaces(false);
                }
                break;
        }
    }

    public void openEnterPin() {
        player.getInterfaceSettings().openInterface(ENTER_PIN_INTERFACE);
        player.getFrames().modifyText("Click the 1st digit...", 13, 151);
        player.getFrames().modifyText("", 13, 150);
        for (int i = 0; i < 3; i++) {
            player.getFrames().modifyText("?", 13, 140 + i);
        }
        scramble();
    }

    public int getPinForIndex(int index) {
        int packed = player.getAttributes().getInt("bank_pin_hash");
        int pin = -1;
        switch (index) {
            case 0:
                pin = packed & 0xff;
                break;
            case 1:
                pin = packed >> 8 & 0xff;
                break;
            case 2:
                pin = packed >> 16 & 0xff;
                break;
            case 3:
                pin = packed >> 24 & 0xff;
                break;
        }
        return pin;
    }

    public void scramble() {
        for (int i = 0; i < 10; i++) {
            numbers[i] = i;
            player.getFrames().modifyText("" + i, 13, 110 + i);
        }
    }
}
