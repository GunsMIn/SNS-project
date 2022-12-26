package com.example.crudpersional.repository.customRepository;

import com.example.crudpersional.domain.dto.post.PostSelectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    public Page<PostSelectResponse> selectBoardList(String searchVal, Pageable pageable);
}
