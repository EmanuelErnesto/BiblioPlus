package com.emanuel.BiblioPlus.modules.orders.services;

import com.emanuel.BiblioPlus.modules.books.infra.database.entities.BookModel;
import com.emanuel.BiblioPlus.modules.books.infra.database.repositories.BookRepository;
import com.emanuel.BiblioPlus.modules.books.services.BookService;
import com.emanuel.BiblioPlus.modules.orders.domain.dtos.request.CreateOrderDTO;
import com.emanuel.BiblioPlus.modules.orders.domain.mappers.OrderMapper;
import com.emanuel.BiblioPlus.modules.orders.infra.database.entities.BookAndUserReturnFromDB;
import com.emanuel.BiblioPlus.modules.orders.infra.database.entities.OrderModel;
import com.emanuel.BiblioPlus.modules.orders.infra.database.entities.OrderStatus;
import com.emanuel.BiblioPlus.modules.orders.infra.database.repositories.OrderRepository;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
import com.emanuel.BiblioPlus.modules.users.services.UserServiceImpl;
import com.emanuel.BiblioPlus.shared.consts.OrderExceptionConsts;
import com.emanuel.BiblioPlus.shared.exceptions.HttpBadRequestException;
import com.emanuel.BiblioPlus.shared.exceptions.HttpNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Transactional
    public OrderModel create(CreateOrderDTO createOrderDTO) {

        var bookAndUserRetrievedFromDB = validateIfUserAndBookExists(createOrderDTO.getUserId(), createOrderDTO.getBookId());

        LocalDate start_date = OrderMapper.mappingStringLocalDateToLocalDate(createOrderDTO.getStartDate());
        LocalDate end_date = OrderMapper.mappingStringLocalDateToLocalDate(createOrderDTO.getEndDate());

       validateIfStartDateAndEndDateIsNotInPast(start_date, end_date);
       validateIfStartDateIsBeforeEndDate(start_date, end_date);

        Optional<OrderModel> userAlreadyHaveOrderInInterval = findOrderByInterval(
                createOrderDTO.getUserId(), start_date, end_date
        );

        if(userAlreadyHaveOrderInInterval.isPresent() &&
                Objects.equals(userAlreadyHaveOrderInInterval.get().getOrderStatus().toString(), "DELIVERED")) {
            throw new HttpBadRequestException(
                    OrderExceptionConsts.USER_ALREADY_HAVE_ORDER_IN_THIS_PERIOD);
        }

        Optional<OrderModel> userHaveOrderWithOrderStatusDelivered = orderRepository
                .findOrderByUserAndOrderStatus(UUID.fromString(createOrderDTO.getUserId()));

        if(userHaveOrderWithOrderStatusDelivered.isPresent()) {
            throw new HttpBadRequestException(OrderExceptionConsts.USER_HAVE_ORDER_PENDENT_TO_RETURN);
        }

        BookModel book =  bookAndUserRetrievedFromDB.getBook();

        if(book.getQuantityInStock() == 0) {
            throw new HttpBadRequestException(OrderExceptionConsts.BOOK_OUT_OF_STOCK);
        }

        book.setQuantityInStock(book.getQuantityInStock() - 1);

        OrderModel orderToCreate = OrderMapper.mappingCreateOrderDTOToOrderEntity(createOrderDTO);
        orderToCreate.setUser(bookAndUserRetrievedFromDB.getUser());
        orderToCreate.setBook(bookAndUserRetrievedFromDB.getBook());

        bookRepository.save(book);
        return orderRepository.save(orderToCreate);
    }

    @Transactional(readOnly = true)
    public Page<OrderModel> list(Integer pageNumber, Integer size) {
        return orderRepository.findAll(PageRequest.of(pageNumber, size));
    }

    @Transactional(readOnly = true)
    public OrderModel show(String id) {
        return orderRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new HttpNotFoundException(OrderExceptionConsts.ORDER_NOT_FOUND)
        );
    }

    @Transactional
    public void delete(String id) {
        OrderModel order = show(id);

        orderRepository.delete(order);
    }

    @Transactional
    public void updateOrderStatus(String orderId, String userId, String bookId) {
        OrderModel orderToUpdate = show(orderId);

        var bookAndUserData = validateIfUserAndBookExists(userId, bookId);


        orderToUpdate.setOrderStatus(OrderStatus.RETURNED);

        BookModel book = bookAndUserData.getBook();

        book.setQuantityInStock(book.getQuantityInStock() + 1);

        bookRepository.save(book);
        orderRepository.save(orderToUpdate);
    }

    @Transactional
    public OrderModel update(String id, CreateOrderDTO orderDTO){
        var bookAndUserRetrievedFromDB = validateIfUserAndBookExists(orderDTO.getUserId(), orderDTO.getBookId());

        LocalDate start_date = OrderMapper.mappingStringLocalDateToLocalDate(orderDTO.getStartDate());
        LocalDate end_date = OrderMapper.mappingStringLocalDateToLocalDate(orderDTO.getEndDate());

        BookModel bookFromIdRequest = bookAndUserRetrievedFromDB.getBook();

        validateIfStartDateAndEndDateIsNotInPast(start_date, end_date);
        validateIfStartDateIsBeforeEndDate(start_date, end_date);

        OrderModel order = show(id);

        BookModel oldBookFromOrder = order.getBook();

        if(!Objects.equals(order.getUser().getId(), UUID.fromString(orderDTO.getUserId()))) {
            throw new HttpBadRequestException(OrderExceptionConsts.ORDER_SHOULD_NOT_CHANGE_USER);
        }

        OrderMapper.mappingUpdateOrderDataToOrderModel(order, orderDTO);

        if(!Objects.equals(oldBookFromOrder.getId(), bookFromIdRequest.getId())) {
            Long stockFromOldBook = oldBookFromOrder.getQuantityInStock();
            Long stockFromNewBook = bookFromIdRequest.getQuantityInStock();

            if(stockFromNewBook == 0) {
                throw new HttpBadRequestException(OrderExceptionConsts.BOOK_OUT_OF_STOCK);
            }
            bookFromIdRequest.setQuantityInStock(stockFromNewBook - 1);
            oldBookFromOrder.setQuantityInStock(stockFromOldBook + 1);

            order.setBook(bookFromIdRequest);
            bookRepository.saveAndFlush(oldBookFromOrder);
            bookRepository.saveAndFlush(bookFromIdRequest);
        }

        return orderRepository.save(order);
    }

    private Optional<OrderModel> findOrderByInterval(String userId,LocalDate startDate, LocalDate endDate) {
        return orderRepository
                .findOrderByStartAndEndDate(UUID.fromString(userId), startDate, endDate);
    }

    private BookAndUserReturnFromDB validateIfUserAndBookExists(String userId, String bookId) {
       UserModel user = userService.show(userId);
       BookModel book = bookService.show(bookId);

       return new BookAndUserReturnFromDB(user, book);
    }

    private void validateIfStartDateIsBeforeEndDate(LocalDate startDate, LocalDate endDate) {
        if(startDate.isAfter(endDate)) {
            throw new HttpBadRequestException(OrderExceptionConsts.ORDER_START_DATE_CANNOT_AFTER_END);
        }

        if(endDate.isBefore(startDate)) {
            throw new HttpBadRequestException(OrderExceptionConsts.ORDER_END_DATE_CANNOT_BEFORE_START);

        }

    }

    private void validateIfStartDateAndEndDateIsNotInPast(LocalDate startDate, LocalDate endDate) {
        if(startDate.isBefore(LocalDate.now())) {
            throw new HttpBadRequestException(OrderExceptionConsts.ORDER_START_DATE_CANNOT_BE_IN_PAST);
        }
        if(endDate.isBefore(LocalDate.now())) {
            throw new HttpBadRequestException(OrderExceptionConsts.ORDER_END_DATE_CANNOT_BE_IN_PAST);
        }
    }
}
