package com.example.last.controller;

import com.example.last.config.SecurityUtil;
import com.example.last.dto.BoardDto;
import com.example.last.entity.Board;
import com.example.last.entity.User;
import com.example.last.repository.BoardRepository;
import com.example.last.repository.UserRepository;
import com.example.last.response.Response;
import com.example.last.response.ResponsePage;
import com.example.last.service.BoardService;
import com.sun.xml.bind.v2.TODO;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;
    private final UserRepository userRepository;

    private final BoardRepository boardRepository;

    //전체 게시글 조회
    @GetMapping("/api/boards")
    @ApiOperation(value = "전체 게시글 조회", notes = "등록된 전체 게시글을 조회하는 API입니다.")
    public ResponsePage getBoardsPaging(Pageable pageable) {
        return new ResponsePage("성공", "전체 게시글 리턴", boardService.getBoardsPaging(pageable),
                String.valueOf(boardService.getBoardPaging(pageable).getTotalPages()
                ), String.valueOf(boardService.getBoardPaging(pageable).getTotalElements()));
    }

    //내 게시글 조회
    @GetMapping("api/myboards")
    @ApiOperation(value = "내 게시글 조회", notes = "자신이 등록한 전체 게시글을 조회하는 API입니다.")
    public ResponsePage getMyBoards(Pageable pageable, Authentication authentication) {
        return new ResponsePage("성공", "내 게시글 리턴", boardService.getMyBoards(pageable, authentication),
                String.valueOf(boardService.getMyBoardPaging(pageable, authentication).getTotalPages()
                ), String.valueOf(boardService.getMyBoardPaging(pageable,authentication).getTotalElements()));
    }

    // 개별 게시글 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/boards/{id}")
    @ApiOperation(value = "개별 게시글 조회", notes = "원하는 게시글을 조회하는 API입니다.")
    public Response getBoard(@PathVariable("id") Integer id, Authentication authentication) {
        // 해당 게시글의 작성자 확인
        Optional<Board> findBoard = boardRepository.findById(id);
        if (Long.valueOf(authentication.getName()) == findBoard.get().getUser().getId()) {
            return new Response("성공", "내 게시글", boardService.getBoard(id));
        } else {
            return new Response("성공", "타인 게시글", boardService.getBoard(id));
        }
    }

    // 게시글 작성
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/boards/write")
    @ApiOperation(value = "게시글 작성", notes = "신규 게시글을 등록하는 API입니다.")
    public Response write(@RequestBody BoardDto boardDto, Authentication authentication) {
        // 원래 로그인을 하면, User 정보는 세션을 통해서 구하고 주면 되지만,
        // 지금은 핵심 개념을 알기 위해서, JWT 로그인은 생략하고, 임의로 findById 로 유저 정보를 넣어줬습니다.
        User user = userRepository.findById(Integer.valueOf(authentication.getName())).get();

        return new Response("성공", "글 작성 성공", boardService.write(boardDto, user));
    }



    // 게시글 수정
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/api/boards/update/{id}")
    @ApiOperation(value = "게시글 수정", notes = "자신이 작성한 게시글 중에 원하는 게시글을 수정하는 API입니다.")
    public Response edit(@RequestBody BoardDto boardDto, @PathVariable("id") Integer id, Authentication authentication) {
        // 원래 로그인을 하면, User 정보는 세션을 통해서 구하고 주면 되지만,
        // 지금은 핵심 개념을 알기 위해서, JWT 로그인은 생략하고, 임의로 findById 로 유저 정보를 넣어줬습니다.

        // 추후에 JWT 로그인을 배우고나서 적용해야할 것

        // 1. 현재 요청을 보낸 유저의 JWT 토큰 정보(프론트엔드가 헤더를 통해 보내줌)를 바탕으로
        // 현재 로그인한 유저의 정보가 PathVariable로 들어오는 BoardID 의 작성자인 user정보와 일치하는지 확인하고
        // 맞으면 아래 로직 수행, 틀리면 다른 로직(ResponseFail 등 커스텀으로 만들어서) 수행
        // 이건 if문으로 처리할 수 있습니다. * 이 방법 말고 service 내부에서 확인해도 상관 없음


        // 해당 게시글의 작성자 확인
        Optional<Board> findBoard = boardRepository.findById(id);


        if (Long.valueOf(authentication.getName()) == findBoard.get().getUser().getId()) {
            return new Response("성공", "글 수정 성공", boardService.update(id, boardDto));
        } else {
            throw new RuntimeException("계정 정보가 맞지 않습니다.");
        }
    }

    // 게시글 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/api/boards/delete/{id}")
    @ApiOperation(value = "게시글 삭제", notes = "자신이 작성한 게시글 중에 원하는 게시글을 삭제하는 API입니다.")
    public Response delete(@PathVariable("id") Integer id, Authentication authentication) {
        Optional<Board> findBoard = boardRepository.findById(id);

        if (Long.valueOf(authentication.getName()) == findBoard.get().getUser().getId()) {
            boardService.delete(id); // 이 메소드는 반환값이 없으므로 따로 삭제 수행해주고, 리턴에는 null을 넣어줌
            return new Response("성공", "글 삭제 성공", null);
        } else {
            throw new RuntimeException("계정 정보가 맞지 않습니다.");
        }

    }
}