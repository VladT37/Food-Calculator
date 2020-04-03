public class NameEditor {

    public static String replaceMultipleWhitespaces(String name) {
        // to replace multiple whitespaces and non-visible characters

        name = name.trim().replaceAll("\\s{2,}", " ");

        return name;
    }

    public static String capitalizeFirstLetter(String name) {
        // returns the string after capitalizing the first letter from it

        if (name.length() > 0) {
            char firstLetter = name.charAt(0);
            name = name.substring(1);
            name = Character.toUpperCase(firstLetter) + name;
        }

        return name;
    }

    public static String capitalizeWords(String name) {
        // returns the string after capitalizing each word's first letter from it

        name = capitalizeFirstLetter(name);
        if (name.length() > 1) {
            for (int i = 1; i < name.length() - 1; i++) {
                if (name.charAt(i) == ' ') {
                    if ((name.codePointAt(i + 1) >= (int) 'a') && (name.codePointAt(i + 1) <= (int) 'z')) {
                        char upper = name.charAt(i + 1);
                        String firstPart = name.substring(0, i);
                        String secondPart = name.substring(i + 2);
                        name = firstPart + " " + Character.toUpperCase(upper) + secondPart;
                        i++;
                    }
                }
            }
        }

        return name;
    }
}
