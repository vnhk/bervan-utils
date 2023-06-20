package com.bervan.history.diff.service;

import com.bervan.history.diff.model.DiffType;
import com.bervan.history.diff.model.DiffWord;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class DiffResultParser {

    public static String beauty(List<DiffWord> diff) {
        if (CollectionUtils.isEmpty(diff)) {
            return "";
        }

        return beautyBuild(diff);
    }

    private static String beautyBuild(List<DiffWord> diff) {
        StringBuilder stringBuilder = new StringBuilder();

        DiffType prevType = diff.get(0).getType();
        for (DiffWord diffWord : diff) {
            String value = diffWord.getValue();
            DiffType type = diffWord.getType();

            if (!type.equals(prevType)) {
                stringBuilder.append(type.getDisplayName());
                prevType = type;
            }

            stringBuilder.append(value);
            stringBuilder.append(" ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        return stringBuilder.toString();
    }
}