package com.example.springproject.service;

import com.example.springproject.entity.Category;
import com.example.springproject.entity.Item;
import com.example.springproject.entity.ItemStatus;
import com.example.springproject.entity.User;
import com.example.springproject.repository.ItemRepository;
import com.example.springproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserContext context;

    public void createItem(String title, Category category) {
        context.checkNotBanned();
        User owner = context.getCurrentUser();

        Item item = new Item();
        item.setOwner(owner);
        item.setTitle(title);
        item.setCategory(category);

        itemRepository.save(item);
    }

    public List<Item> findAvailableItemsForUser() {
        User currentUser = context.getCurrentUser();

        return itemRepository.findAvailableItemsForUser(currentUser.getUserId(), ItemStatus.available);
    }

    public List<Item> findItemsForOwner() {
        User currentUser = context.getCurrentUser();

        return itemRepository.findByOwnerUserId(currentUser.getUserId());
    }

}
