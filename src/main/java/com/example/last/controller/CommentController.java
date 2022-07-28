package com.example.last.controller;


import com.example.last.dto.CommentDto;
import com.example.last.entity.Board;
import com.example.last.entity.Comment;
import com.example.last.entity.User;
import com.example.last.repository.CommentRepository;
import com.example.last.repository.UserRepository;
import com.example.last.response.Response;
import com.example.last.service.CommentService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    // 댓글 작성
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/api/comments/{boardId}")
    @ApiOperation(value = "댓글 작성", notes = "원하는 게시글에 신규 댓글을 등록하는 API입니다.")
    public Response writeComment(@PathVariable("boardId") Integer boardId,
                                 @RequestBody CommentDto commentDto, Authentication authentication) {
        User user = userRepository.findById(Integer.valueOf(authentication.getName())).get();
        return new Response("성공", "댓글 작성을 완료했습니다.",
                commentService.writeComment(boardId, commentDto, user));
    }

    // 게시글에 달린 전체 댓글 조회

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/comments/{boardId}")
    @ApiOperation(value = "게시글에 달린 전체 댓글 조회", notes = "해당 게시글에 작성된 전체 댓글을 조회하는 API입니다.")
    public Response getComments(@PathVariable("boardId") Integer boardId) {
        return new Response("성공", "댓글을 불러왔습니다.", commentService.getComments(boardId));
    }

    // 댓글 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/api/comments/{boardId}/{commentId}")
    @ApiOperation(value = "댓글 삭제", notes = "해당 게시글에 자신이 작성한 댓글 중 원하는 댓글을 삭제하는 API입니다.")
    public Response deleteComment(@PathVariable("boardId") Integer boardId,
                                  @PathVariable("commentId") Integer commentId,
                                  Authentication authentication) {

        // 해당 댓글의 작성자 확인
        Optional<Comment> findComment = commentRepository.findById(commentId);
        if (findComment.get().getUser().getId() == Integer.valueOf(authentication.getName())) {
            return new Response("성공", "댓글 삭제 완료", commentService.deleteComment(commentId));
        }
        else {
            return new Response("실패", "댓글 작성자가 아닙니다.", null);
        }
    }

}
