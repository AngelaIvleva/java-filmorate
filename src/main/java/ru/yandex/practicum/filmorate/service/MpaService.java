package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.rating.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public Mpa getById(int id) {
        return mpaStorage.getById(id);
    }

    public List<Mpa> getAll() {
        return mpaStorage.getAll();
    }
}
