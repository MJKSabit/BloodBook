package com.memoryleak.bloodbank.config;

public class View {
    public static class Public { }
    public static class ExtendedPublic extends Public { }
    public static class Private extends ExtendedPublic { }
}
