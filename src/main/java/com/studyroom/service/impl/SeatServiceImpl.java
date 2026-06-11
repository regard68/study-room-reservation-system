package com.studyroom.service.impl;

import com.studyroom.entity.Seat;
import com.studyroom.mapper.SeatMapper;
import com.studyroom.mapper.StudyRoomMapper;
import com.studyroom.service.SeatService;
import com.studyroom.utils.BusinessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatServiceImpl implements SeatService {
    private final SeatMapper seatMapper;
    private final StudyRoomMapper roomMapper;

    public SeatServiceImpl(SeatMapper seatMapper, StudyRoomMapper roomMapper) {
        this.seatMapper = seatMapper;
        this.roomMapper = roomMapper;
    }

    public List<Seat> findAll() { return seatMapper.findAll(); }
    public List<Seat> findByRoomId(Long roomId) { return seatMapper.findByRoomId(roomId); }
    public Seat findById(Long id) { return seatMapper.findById(id); }

    @Override
    @Transactional
    public void save(Seat seat) {
        if (seatMapper.countDuplicate(seat.getRoomId(), seat.getSeatNo(), seat.getId()) > 0) {
            throw new BusinessException("该自习室中座位号已存在");
        }
        if (seat.getStatus() == null) seat.setStatus("AVAILABLE");
        Seat oldSeat = seat.getId() == null ? null : seatMapper.findById(seat.getId());
        if (seat.getId() == null) seatMapper.insert(seat); else seatMapper.update(seat);
        roomMapper.refreshSeatCount(seat.getRoomId());
        if (oldSeat != null && !oldSeat.getRoomId().equals(seat.getRoomId())) {
            roomMapper.refreshSeatCount(oldSeat.getRoomId());
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Seat seat = seatMapper.findById(id);
        if (seat == null) return;
        try {
            seatMapper.delete(id);
            roomMapper.refreshSeatCount(seat.getRoomId());
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("该座位已有预约记录，不能删除");
        }
    }
}
