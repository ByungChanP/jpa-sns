package net.likelion.bebc25.jpasns.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.jpasns.domain.Comment;
import net.likelion.bebc25.jpasns.domain.Member;
import net.likelion.bebc25.jpasns.domain.Post;
import net.likelion.bebc25.jpasns.dto.CommentDto;
import net.likelion.bebc25.jpasns.dto.PostDto;
import net.likelion.bebc25.jpasns.repository.CommentRepository;
import net.likelion.bebc25.jpasns.repository.MemberRepository;
import net.likelion.bebc25.jpasns.repository.PostRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PostDto.Response createPost(Long memberId, PostDto.CreateRequest request){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. id=" + memberId));
        Post post = new Post(member, request.content());
        Post savedPost = postRepository.save(post);
        return PostDto.Response.from(savedPost);
    }
    public PostDto.DetailResponse getPost(Long id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다. id=" + id));
        return PostDto.DetailResponse.from(post);
    }
    // 전체 게시글 목록 조회 페이징
    public Page<PostDto.Response> getAllPosts(Pageable pageable){
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(PostDto.Response :: from);
    }

    // 전체 게시글 목록 조회
    public Page<PostDto.Response> getPosts(Long memberId, String keyword, Pageable pageable){
        Page<Post> posts;
        boolean hasMember = memberId != null;
        boolean hasKeyword = keyword != null && !keyword.isBlank();



        if(hasMember){ // memberId가 있을 경우
            Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
            // 회원 존재 여부를 먼저 확인한 뒤 해당 회원의 게시글 목록을 조회하여 DTO로 변환하여 반환
            if(!memberRepository.existsById(memberId)){
                throw new IllegalArgumentException("존재하지 않는 회원입니다. id=" + memberId);
            }

            if(hasKeyword){
                posts = postRepository.findByMemberAndKeyword(member, keyword, pageable);
            }else{
                posts = postRepository.findByMemberOrderByCreatedAtDesc(member, pageable);
            }
        }else{ // memberId가 없을 경우
            if(hasKeyword){
                posts = postRepository.findByContentContainingOrderByCreatedAtDesc(keyword, pageable);
            }else{
                posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
        }

        return posts.map(PostDto.Response::from);
    }

    @Transactional
    public CommentDto.Response addComment(Long postId, Long memberId, CommentDto.CreateRequest request){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다. id=" + postId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. id=" + memberId));


        Comment comment = new Comment(member, request.content());
        // 영속성 컨텍스트의 동기화와 Comment에 Post 정보를 추가하기 위해 필요
        post.addComment(comment);
        Comment savedComment = commentRepository.save(comment);

        return CommentDto.Response.from(savedComment);
    }
}