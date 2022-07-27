package com.example.last.service;

import com.example.last.dto.BoardDto;
import com.example.last.entity.Board;
import com.example.last.entity.User;
import com.example.last.repository.BoardRepository;
import com.example.last.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;


    // 전체 게시글 페이지 조회
    @Transactional(readOnly = true)
    public List<BoardDto> getBoardsPaging(Pageable pageable) {
        Page<Board> boardPage = boardRepository.findAll(pageable);
        List<BoardDto> boardDtos = new ArrayList<>();
        boardPage.forEach(s -> boardDtos.add(BoardDto.toDto(s)));
        return boardDtos;
    }


    // 게시글 페이지 조회
    @Transactional(readOnly = true)
    public Page<Board> getBoardPaging(Pageable pageable) {
        Page<Board> boardPage = boardRepository.findAll(pageable);
        return boardPage;
    }


    //내 게시글 조회
    public List<BoardDto> getMyBoards(Pageable pageable, Authentication authentication) {
        Integer userId = Integer.valueOf(authentication.getName());
        User user = userRepository.findById(userId).get();
        Page<Board> boardPage = boardRepository.findByUser(pageable,user);
        List<BoardDto> boardDtos = new ArrayList<>();
        for (Board s : boardPage) {
                boardDtos.add(BoardDto.toDto(s));
        }
        return boardDtos;
    }

    //내 게시글 페이지
    @Transactional(readOnly = true)
    public Page<Board> getMyBoardPaging(Pageable pageable, Authentication authentication) {
        Integer userId = Integer.valueOf(authentication.getName());
        User user = userRepository.findById(userId).get();
        Page<Board> boardMyPage = boardRepository.findByUser(pageable, user);
        return boardMyPage;
    }



    // 개별 게시글 조회
    @Transactional
    public BoardDto getBoard(int id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> {
            return new IllegalArgumentException("Board Id를 찾을 수 없습니다.");
        });

        // 개별 게시글 조회 시, 카운트 증가
        int views = board.getViews() + 1;
        board.setViews(views);
        boardRepository.save(board);

        BoardDto boardDto = BoardDto.toDto(board);
        return boardDto;
    }

    // 게시글 작성
    @Transactional
    public BoardDto write(BoardDto boardDto, User user) {
        Board board = new Board();
        board.setTitle(boardDto.getTitle());
        board.setContent(boardDto.getContent());
        board.setFile(boardDto.getFile());
        board.setUser(user);
        boardRepository.save(board);
        return BoardDto.toDto(board);
    }

    // 게시글 수정
    @Transactional
    public BoardDto update(int id, BoardDto boardDto) {
        Board board = boardRepository.findById(id).orElseThrow(() -> {
            return new IllegalArgumentException("Board Id를 찾을 수 없습니다!");
        });

        board.setTitle(boardDto.getTitle());
        board.setContent(boardDto.getContent());
        board.setFile(boardDto.getFile());

        return BoardDto.toDto(board);
    }


    // 게시글 삭제
    @Transactional
    public void delete(int id) {
        // 매개변수 id를 기반으로, 게시글이 존재하는지 먼저 찾음
        // 게시글이 없으면 오류 처리
        Board board = boardRepository.findById(id).orElseThrow(() -> {
            return new IllegalArgumentException("Board Id를 찾을 수 없습니다!");
        });

        // 게시글이 있는 경우 삭제처리
        boardRepository.deleteById(id);

    }


}