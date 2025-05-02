public class FlashcardApp {
    public static void main(String[] args) {
        String order = "random";
        int repetitions = 1;
        boolean invert = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--help":
                    System.out.println("--help Show help information\n" +
                                       "--order <order> Sorting type [random, worst-first, recent-mistakes-first]\n" +
                                       "--repetitions <num> Number of times to answer a card correctly\n" +
                                       "--invertCards Invert Q&A");
                    return;
                case "--order":
                    order = args[++i];
                    break;
                case "--repetitions":
                    repetitions = Integer.parseInt(args[++i]);
                    break;
                case "--invertCards":
                    invert = true;
                    break;
            }
        }

        FlashcardSession session = new FlashcardSession(order, repetitions, invert);
        session.run();
    }
}