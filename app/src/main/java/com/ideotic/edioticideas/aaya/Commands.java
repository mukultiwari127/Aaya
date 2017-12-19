package com.ideotic.edioticideas.aaya;

/**
 * Created by Mukul on 13-05-2016.
 */
public class Commands {

    //Main Activity Commands
    public static final String mailModule = "mail";
    public static final String callModule = "phone";
    public static final String locModule = "location";
    public static final String remmodule = "reminder";
    public static final String musicModule = "play music";
    public static final String emergencyModule = "emergency";
    public static final String noteModule = "note";
    public static final String helpModule = "help";
    public static final String EMERGENCY = "emergency activity";
    public static final String TIME = "time";

    //Gmail Module Commands
    //search base commands
    public static final String MAIL_FETCH_MAILS = "get mails";
    public static final String MAIL_FEtCH_LABELS = "get labels";
    public static final String MAIL_SEARCH_SUBJECT = "search subject";
    public static final String MAIL_SEARCH_LABELS = "search labels";
    public static final String MAIL_NEXT = "next";
    public static final String MAIL_CONFIRM = "do you want to change any field?";


    //compose based commands
    public static final String MAIL_COMPOSE_MAIL = "compose mail";

    //Weather Module
    public static final String TEMP = "s";
    public static final String WEEK = "w";
    public static final String weather = "how is the weather";

    //Support commands
    public static final String MAIL_SEND = "send";
    public static final String MAIL_SUBJECT = "subject";
    public static final String MAIL_TO = "send to";
    public static final String MAIL_BODY = "body";

    //Phone Module Commands
    public static final String CALL_LOG = "call logs";
    public static final String SMS = "messages";
    public static final String CALL = "call";
    public static final String CONTACTS = "search contact";
    public static final String SMS_SEND = "send message";
    public static final String PHONE_HELP = "help";

    //Reminder Module Commands
    public static final String DATE = "date";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String HOUR = "hour";
    public static final String MINUTES = "minutes";
    public static final String TITLE = "title";
    public static final String SET = "setting";

    //Notes Module in Utility
    public static final String TITLEn = "title";
    public static final String BODY = "body";
    public static final String PRIORITY = "priority";
    public static final String SAVE = "saving";
    public static final String MAKE = "make";
    public static final String READ = "read";
    public static final String YES = "yes";
    public static final String NO = "no";

    public static String[] filterCommands(String commandToFilter) {
        String words[] = commandToFilter.split(" ");
        return switchCommands(words);

    }


    public static String[] switchCommands(String[] commandToSwitch) {
        String[] a = new String[4];
        String as = "";
        if (commandToSwitch[0].equals(CALL)) {
            if (commandToSwitch.length == 4) {
                switch (commandToSwitch[0]) {
                    case CALL:
                        if (commandToSwitch[1] != null) {
                            a[0] = commandToSwitch[0];
                            a[1] = commandToSwitch[1] + commandToSwitch[2] + commandToSwitch[3];
                            return a;
                        }
                        break;
                }
            } else if (commandToSwitch[1].equals("logs")) {
                a[0] = commandToSwitch[0] + " " + commandToSwitch[1];
            } else {
                a[0] = commandToSwitch[0];
                a[1] = commandToSwitch[1];
                a[2] = "check";
            }
        } else if (commandToSwitch[0].equals(Commands.SMS)) {
            a[0] = commandToSwitch[0];
        } else if (commandToSwitch[0].equals(Commands.helpModule)) {
            a[0] = commandToSwitch[0];
        } else if (commandToSwitch[0].equals(Commands.PHONE_HELP)) {
            a[0] = commandToSwitch[0];
            a[1] = "";
        } else if (commandToSwitch.length == 1) {
            a[0] = "";
        } else {
            switch (commandToSwitch[0] + " " + commandToSwitch[1]) {
                case MAIL_SEARCH_SUBJECT:
                    a[0] = commandToSwitch[0] + " " + commandToSwitch[1];
                    if (commandToSwitch[2] != null) {
                        a[1] = commandToSwitch[2];
                    }
                    break;
                case MAIL_FETCH_MAILS:
                    a[0] = commandToSwitch[0] + " " + commandToSwitch[1];
                    try {
                        a[1] = commandToSwitch[2];
                    } catch (IndexOutOfBoundsException e) {
                        a[1] = "";
                    }
                    break;
                case CONTACTS:
                    a[0] = commandToSwitch[0] + " " + commandToSwitch[1];
                    if (commandToSwitch[2] != null) {
                        a[1] = commandToSwitch[2];
                    }
                    break;
                default:
                    for (String ab : commandToSwitch)
                        as = as + ab + " ";
                    a[0] = as.trim();
            }
        }
        return a;
    }


}
