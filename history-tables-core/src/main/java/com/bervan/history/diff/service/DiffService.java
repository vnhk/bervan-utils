package com.bervan.history.diff.service;

import com.bervan.history.diff.model.DiffType;
import com.bervan.history.diff.model.DiffWord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DiffService {

    private final static String SPACE_REGEX = " ";

    public static List<DiffWord> diff(String fst, String snd) {
        if (fst == null && snd != null) {
            return Arrays.asList(new DiffWord(snd, DiffType.ADDED));
        } else if (fst != null && snd == null) {
            return Arrays.asList(new DiffWord(fst, DiffType.REMOVED));
        } else if (fst == null) {
            return new ArrayList<>();
        }

        String[] wordsFst = fst.split(SPACE_REGEX);
        String[] wordsSnd = snd.split(SPACE_REGEX);

        int fstLen = wordsFst.length;
        int sndLen = wordsSnd.length;

        int[][] tab = prepareTable(wordsFst, wordsSnd);
        List<String> commonWords = getCommonWords(wordsFst, wordsSnd, tab);

        return compare(wordsFst, wordsSnd, fstLen, sndLen, commonWords);
    }

    private static List<DiffWord> compare(String[] wordsFst, String[] wordsSnd, int fstLen, int sndLen, List<String> commonWords) {
        List<DiffWord> result = new ArrayList<>();

        int ai = 0;
        int ri = 0;
        int bi = 0;

        boolean riExceeded = false;
        boolean aiExceeded = false;
        boolean biExceeded = false;

        while (!riExceeded || !aiExceeded || !biExceeded) {
            if (!riExceeded) {
                if (commonWords.size() > ri && wordsFst[ai].equals(commonWords.get(ri))) {
                    if (wordsFst[ai].equals(wordsSnd[bi])) {
                        whenWordsAreEqual(result, wordsFst[ai]);
                        bi++;
                        ai++;
                        ri++;
                    } else {
                        whenAddedToSndString(result, wordsSnd[bi]);
                        bi++;
                    }
                } else {
                    whenRemovedFromFstString(result, wordsFst[ai]);
                    ai++;
                }

                if (!riExceeded && ri >= commonWords.size()) {
                    riExceeded = true;
                }
            } else {
                if (aiExceeded) {
                    whenAddedToSndString(result, wordsSnd[bi]);
                    bi++;
                } else {
                    whenRemovedFromFstString(result, wordsFst[ai]);
                    ai++;
                }
            }

            if (!aiExceeded && ai >= fstLen) {
                aiExceeded = true;
            }

            if (!biExceeded && bi >= sndLen) {
                biExceeded = true;
            }
        }

        return result;
    }

    private static List<String> getCommonWords(String[] wordsFst, String[] wordsSnd, int[][] tab) {
        int a = wordsFst.length;
        int b = wordsSnd.length;

        List<String> commonWords = new LinkedList<>();

        while (tab[a][b] != 0) {
            if (!wordsFst[a - 1].equals(wordsSnd[b - 1])) {
                if (tab[a][b - 1] >= tab[a - 1][b]) {
                    b--;
                } else {
                    a--;
                }
            } else {
                commonWords.add(0, wordsFst[a - 1]);
                b--;
                a--;
            }
        }

        return commonWords;
    }

    private static void whenRemovedFromFstString(List<DiffWord> result, String word) {
        if (word.length() > 0) {
            result.add(new DiffWord(word, DiffType.REMOVED));
        }
    }

    private static void whenAddedToSndString(List<DiffWord> result, String word) {
        if (word.length() > 0) {
            result.add(new DiffWord(word, DiffType.ADDED));
        }
    }

    private static void whenWordsAreEqual(List<DiffWord> result, String word) {
        if (word.length() > 0) {
            result.add(new DiffWord(word, DiffType.EQUAL));
        }
    }

    private static int[][] prepareTable(String[] wordsFst, String[] wordsSnd) {
        int fstLen = wordsFst.length;
        int sndLen = wordsSnd.length;

        int[][] tab = initTabWithZeroes(fstLen, sndLen);

        for (int a = 1; a <= fstLen; a++) {
            for (int b = 1; b <= sndLen; b++) {
                if (wordsFst[a - 1].equals(wordsSnd[b - 1])) {
                    tab[a][b] = tab[a - 1][b - 1] + 1;
                } else {
                    tab[a][b] = max(tab[a][b - 1], tab[a - 1][b]);
                }
            }
        }

        return tab;
    }

    private static int[][] initTabWithZeroes(int fstLen, int sndLen) {
        int[][] tab = new int[fstLen + 1][sndLen + 1];

        for (int[] t : tab) {
            Arrays.fill(t, 0);
        }

        return tab;
    }

    private static int max(int a, int b) {
        return Math.max(a, b);
    }
}
