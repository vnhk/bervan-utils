package com.bervan.history.diff;

import com.bervan.history.diff.model.DiffType;
import com.bervan.history.diff.model.DiffWord;
import com.bervan.history.diff.service.DiffResultParser;
import com.bervan.history.diff.service.DiffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


class DiffServiceTest {

    @Test
    void diffNotEqual_1() {
        String text1 = "Sed";
        String text2 = "Ded!";

        List<DiffWord> diff = DiffService.diff(text1, text2);
        assertThat(diff).hasSize(2);
        assertThat(diff.get(0).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(0).getValue()).isEqualTo("Sed");
        assertThat(diff.get(1).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(1).getValue()).isEqualTo("Ded!");
    }

    @Test
    void diffNotEqual_0_1() {
        String text1 = "";
        String text2 = "Ded";

        List<DiffWord> diff = DiffService.diff(text1, text2);
        assertThat(diff).hasSize(1);
        assertThat(diff.get(0).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(0).getValue()).isEqualTo("Ded");
    }

    @Test
    void diffNotEqual_0_2() {
        String text1 = "";
        String text2 = "Ded Sed";

        List<DiffWord> diff = DiffService.diff(text1, text2);
        assertThat(diff).hasSize(2);
        assertThat(diff.get(0).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(0).getValue()).isEqualTo("Ded");
        assertThat(diff.get(1).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(1).getValue()).isEqualTo("Sed");
    }

    @Test
    void diffNotEqual_1_0() {
        String text1 = "Ded";
        String text2 = "";

        List<DiffWord> diff = DiffService.diff(text1, text2);
        assertThat(diff).hasSize(1);
        assertThat(diff.get(0).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(0).getValue()).isEqualTo("Ded");
    }

    @Test
    void diffNotEqual_2_0() {
        String text1 = "Ded Sed!";
        String text2 = "";

        List<DiffWord> diff = DiffService.diff(text1, text2);
        assertThat(diff).hasSize(2);
        assertThat(diff.get(0).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(0).getValue()).isEqualTo("Ded");
        assertThat(diff.get(1).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(1).getValue()).isEqualTo("Sed!");
    }

    @Test
    void diffNotEqual_1_2() {
        String text1 = "Sed";
        String text2 = "Ded! Bed";

        List<DiffWord> diff = DiffService.diff(text1, text2);
        assertThat(diff).hasSize(3);
        assertThat(diff.get(0).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(0).getValue()).isEqualTo("Sed");
        assertThat(diff.get(1).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(1).getValue()).isEqualTo("Ded!");
        assertThat(diff.get(2).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(2).getValue()).isEqualTo("Bed");
    }

    @Test
    void diffNotEqual_1_2_01() {
        String text1 = "Sed";
        String text2 = "Ded! Sed";

        List<DiffWord> diff = DiffService.diff(text1, text2);
        assertThat(diff).hasSize(2);
        assertThat(diff.get(0).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(0).getValue()).isEqualTo("Ded!");
        assertThat(diff.get(1).getType()).isEqualTo(DiffType.EQUAL);
        assertThat(diff.get(1).getValue()).isEqualTo("Sed");
    }

    @Test
    void diffNotEqual_1_2_02() {
        String text1 = "Sed";
        String text2 = "Sed Ded!";

        List<DiffWord> diff = DiffService.diff(text1, text2);
        assertThat(diff).hasSize(2);
        assertThat(diff.get(0).getType()).isEqualTo(DiffType.EQUAL);
        assertThat(diff.get(0).getValue()).isEqualTo("Sed");
        assertThat(diff.get(1).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(1).getValue()).isEqualTo("Ded!");
    }

    @Test
    void diffNotEqual_2_1() {
        String text1 = "Ded! Bed";
        String text2 = "Sed";

        List<DiffWord> diff = DiffService.diff(text1, text2);
        assertThat(diff).hasSize(3);
        assertThat(diff.get(0).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(0).getValue()).isEqualTo("Ded!");
        assertThat(diff.get(1).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(1).getValue()).isEqualTo("Bed");
        assertThat(diff.get(2).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(2).getValue()).isEqualTo("Sed");
    }

    @Test
    void diffNotEqual_2_1_01() {
        String text1 = "Ded! Sed";
        String text2 = "Sed";

        List<DiffWord> diff = DiffService.diff(text1, text2);
        assertThat(diff).hasSize(2);
        assertThat(diff.get(0).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(0).getValue()).isEqualTo("Ded!");
        assertThat(diff.get(1).getType()).isEqualTo(DiffType.EQUAL);
        assertThat(diff.get(1).getValue()).isEqualTo("Sed");
    }

    @Test
    void diffNotEqual_2_1_02() {
        String text1 = "Sed Ded!";
        String text2 = "Sed";

        List<DiffWord> diff = DiffService.diff(text1, text2);
        assertThat(diff).hasSize(2);
        assertThat(diff.get(0).getType()).isEqualTo(DiffType.EQUAL);
        assertThat(diff.get(0).getValue()).isEqualTo("Sed");
        assertThat(diff.get(1).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(1).getValue()).isEqualTo("Ded!");
    }

    @Test
    void diffNotEqual_7_2_no_common_words() {
        String text1 = "Project that is used for testing application";
        String text2 = "Edited Desc";

        List<DiffWord> diff = DiffService.diff(text1, text2);
        assertThat(diff).hasSize(9);

        assertThat(diff.get(0).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(0).getValue()).isEqualTo("Project");
        assertThat(diff.get(1).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(1).getValue()).isEqualTo("that");
        assertThat(diff.get(2).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(2).getValue()).isEqualTo("is");
        assertThat(diff.get(3).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(3).getValue()).isEqualTo("used");
        assertThat(diff.get(4).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(4).getValue()).isEqualTo("for");
        assertThat(diff.get(5).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(5).getValue()).isEqualTo("testing");
        assertThat(diff.get(6).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(6).getValue()).isEqualTo("application");
        assertThat(diff.get(7).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(7).getValue()).isEqualTo("Edited");
        assertThat(diff.get(8).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(8).getValue()).isEqualTo("Desc");
    }

    @Test
    void diffNotEqual_2_7_no_common_words() {
        String text1 = "Edited Desc";
        String text2 = "Project that is used for testing application";

        List<DiffWord> diff = DiffService.diff(text1, text2);
        assertThat(diff).hasSize(9);

        assertThat(diff.get(0).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(0).getValue()).isEqualTo("Edited");
        assertThat(diff.get(1).getType()).isEqualTo(DiffType.REMOVED);
        assertThat(diff.get(1).getValue()).isEqualTo("Desc");
        assertThat(diff.get(2).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(2).getValue()).isEqualTo("Project");
        assertThat(diff.get(3).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(3).getValue()).isEqualTo("that");
        assertThat(diff.get(4).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(4).getValue()).isEqualTo("is");
        assertThat(diff.get(5).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(5).getValue()).isEqualTo("used");
        assertThat(diff.get(6).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(6).getValue()).isEqualTo("for");
        assertThat(diff.get(7).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(7).getValue()).isEqualTo("testing");
        assertThat(diff.get(8).getType()).isEqualTo(DiffType.ADDED);
        assertThat(diff.get(8).getValue()).isEqualTo("application");
    }

    @Test
    void diffNotEqual_with_common_words() {
        String text1 = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque atque corrupti quos ipsa quae ab illo";
        String text2 = "Sed accusamus voluptatem accusantium doloremque laudantium, totam ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident";

        List<DiffWord> diff = DiffService.diff(text1, text2);
        String beauty = DiffResultParser.beauty(diff);


        assertThat(beauty).isEqualTo(
                "Sed " +
                        "-ut perspiciatis unde omnis iste natus error sit " +
                        "+accusamus =voluptatem accusantium doloremque laudantium, totam " +
                        "-rem aperiam, eaque +ducimus qui blanditiis praesentium voluptatum deleniti " +
                        "=atque corrupti quos " +
                        "-ipsa quae ab illo " +
                        "+dolores et quas molestias excepturi sint occaecati cupiditate non provident"
        );
    }

    @Test
    void diffWhenEqualBoth() {
        String text = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium! " +
                "- Totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.";

        List<String> words = Arrays.stream(text.split(" ")).collect(Collectors.toList());

        do {
            diffWhenEqualBothHelper(words);
            words = withRemovedLastWord(words);
        } while (words.size() > 0);
    }

    private void diffWhenEqualBothHelper(List<String> words) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String word : words) {
            stringBuilder.append(word);
            stringBuilder.append(" ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        List<DiffWord> diff = DiffService.diff(stringBuilder.toString(), stringBuilder.toString());

        for (int i = 0; i < diff.size(); i++) {
            DiffWord diffWord = diff.get(i);
            assertThat(diffWord.getType()).isEqualTo(DiffType.EQUAL);
            assertThat(diffWord.getValue()).isEqualTo(words.get(i));
        }
    }

    private List<String> withRemovedLastWord(List<String> words) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < words.size() - 1; i++) {
            result.add(words.get(i));
        }

        return result;
    }

}