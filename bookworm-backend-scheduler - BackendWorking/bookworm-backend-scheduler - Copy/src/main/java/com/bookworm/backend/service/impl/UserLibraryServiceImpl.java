package com.bookworm.backend.service.impl;

import com.bookworm.backend.dto.request.BorrowRequest;
import com.bookworm.backend.dto.response.UserLibraryPackageResponse;
import com.bookworm.backend.dto.response.UserLibraryResponse;
import com.bookworm.backend.entity.*;
import com.bookworm.backend.exception.DuplicateResourceException;
import com.bookworm.backend.exception.ResourceNotFoundException;
import com.bookworm.backend.mapper.UserLibraryMapper;
import com.bookworm.backend.repository.*;
import com.bookworm.backend.service.UserLibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Subscribe (pay for a package window) and Borrow (draw a product against
 * that window) are two separate steps, matching the "unified rent+lend
 * access" model: a subscription alone grants nothing on the shelf - only a
 * borrow does, mirroring how RentServiceImpl grants UserShelf per product
 * rather than per transaction.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLibraryServiceImpl implements UserLibraryService {

    private final LibraryPackageRepository libraryPackageRepository;
    private final UserLibraryPackageRepository userLibraryPackageRepository;
    private final UserLibraryRepository userLibraryRepository;
    private final UserShelfRepository userShelfRepository;
    private final ProductRepository productRepository;
    private final UserLibraryMapper mapper;

    @Override
    @Transactional
    public UserLibraryPackageResponse subscribe(Long userId, Long libraryPackageId) {
        userLibraryPackageRepository.findByUserIdAndStatus(userId, UserLibraryPackage.Status.ACTIVE)
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "User already has an active library package subscription");
                });

        LibraryPackage pkg = libraryPackageRepository.findById(libraryPackageId)
                .orElseThrow(() -> new ResourceNotFoundException("LibraryPackage", "library_package_id", libraryPackageId));
        if (!Boolean.TRUE.equals(pkg.getIsActive())) {
            throw new IllegalArgumentException("This library package is no longer available");
        }

        LocalDateTime now = LocalDateTime.now();
        UserLibraryPackage subscription = userLibraryPackageRepository.save(
                UserLibraryPackage.builder()
                        .userId(userId)
                        .libraryPackage(pkg)
                        .startDate(now)
                        .endDate(now.plusDays(pkg.getDurationDays()))
                        .status(UserLibraryPackage.Status.ACTIVE)
                        .build());

        return mapper.toSubscriptionResponse(subscription);
    }

    @Override
    public UserLibraryPackageResponse getActiveSubscription(Long userId) {
        return mapper.toSubscriptionResponse(requireActiveSubscription(userId));
    }

    @Override
    @Transactional
    public UserLibraryResponse borrow(Long userId, BorrowRequest request) {
        UserLibraryPackage subscription = requireActiveSubscription(userId);
        LibraryPackage pkg = subscription.getLibraryPackage();

        long activeBorrows = userLibraryRepository.countByUserLibraryPackage_UserLibraryPackageIdAndStatus(
                subscription.getUserLibraryPackageId(), UserLibrary.Status.BORROWED);
        if (activeBorrows >= pkg.getMaxConcurrentBorrows()) {
            throw new IllegalArgumentException(
                    "Concurrent borrow limit (" + pkg.getMaxConcurrentBorrows() + ") reached for this package");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "product_id", request.getProductId()));
        if (!Boolean.TRUE.equals(product.getIsAvailable()) || !Boolean.TRUE.equals(product.getIsLibraryEligible())) {
            throw new IllegalArgumentException("Product '" + product.getTitle() + "' is not eligible for library borrow");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusDays(request.getBorrowDays());
        if (dueDate.isAfter(subscription.getEndDate())) {
            throw new IllegalArgumentException(
                    "borrowDays would extend past the subscription end date (" + subscription.getEndDate() + ")");
        }

        UserLibrary borrow = userLibraryRepository.save(
                UserLibrary.builder()
                        .userLibraryPackage(subscription)
                        .product(product)
                        .dueDate(dueDate)
                        .status(UserLibrary.Status.BORROWED)
                        .build());

        userShelfRepository.save(UserShelf.builder()
                .userId(userId)
                .product(product)
                .source(UserShelf.Source.LIBRARY)
                .sourceReferenceId(borrow.getUserLibraryId())
                .expiresAt(dueDate)
                .build());

        return mapper.toBorrowResponse(borrow);
    }

    @Override
    @Transactional
    public UserLibraryResponse returnItem(Long userId, Long userLibraryId) {
        UserLibrary borrow = userLibraryRepository.findById(userLibraryId)
                .orElseThrow(() -> new ResourceNotFoundException("UserLibrary", "user_library_id", userLibraryId));
        if (!borrow.getUserLibraryPackage().getUserId().equals(userId)) {
            throw new ResourceNotFoundException("UserLibrary", "user_library_id", userLibraryId);
        }
        if (borrow.getStatus() != UserLibrary.Status.BORROWED) {
            throw new IllegalArgumentException("This item has already been returned or has expired");
        }

        borrow.setStatus(UserLibrary.Status.RETURNED);
        userLibraryRepository.save(borrow);

        // Pull it off the shelf immediately rather than waiting for the expiry job.
        userShelfRepository.findBySourceAndSourceReferenceId(UserShelf.Source.LIBRARY, borrow.getUserLibraryId())
                .ifPresent(userShelfRepository::delete);

        return mapper.toBorrowResponse(borrow);
    }

    @Override
    public List<UserLibraryResponse> getActiveBorrows(Long userId) {
        UserLibraryPackage subscription = requireActiveSubscription(userId);
        return userLibraryRepository
                .findByUserLibraryPackage_UserLibraryPackageIdAndStatus(
                        subscription.getUserLibraryPackageId(), UserLibrary.Status.BORROWED)
                .stream()
                .map(mapper::toBorrowResponse)
                .toList();
    }

    private UserLibraryPackage requireActiveSubscription(Long userId) {
        return userLibraryPackageRepository.findByUserIdAndStatus(userId, UserLibraryPackage.Status.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("User has no active library package subscription"));
    }
}
