package com.repository;

import com.entities.ItemRanking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryItemRankingRepository implements ItemRankingRepository {

    private final List<ItemRanking> store = new ArrayList<>();

    @Override
    public synchronized void replaceAll(List<ItemRanking> ranking) {
        store.clear();
        if (ranking != null)
            store.addAll(ranking);
    }

    @Override
    public synchronized List<ItemRanking> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(store));
    }
}
