/**
 * The MIT License
 * Copyright (c) 2018 Estonian Information System Authority (RIA),
 * Nordic Institute for Interoperability Solutions (NIIS), Population Register Centre (VRK)
 * Copyright (c) 2015-2017 Estonian Information System Authority (RIA), Population Register Centre (VRK)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.niis.xroad.restapi.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.niis.xroad.restapi.domain.PersistentApiKeyType;
import org.niis.xroad.restapi.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test api key service caching while mocking DB
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@Slf4j
@Transactional
public class ApiKeyServiceCachingIntegrationTest {

    @Autowired
    private ApiKeyService apiKeyService;

    @MockBean
    private EntityManager entityManager;

    @Mock
    private Session session;

    @Mock
    private Query query;

    @Test
    @WithMockUser
    public void testList() {
        when(entityManager.unwrap(any())).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.list()).thenReturn(new ArrayList());
        apiKeyService.listAll();
        apiKeyService.listAll();
        verify(query, times(1)).list();
    }

    @Test
    @WithMockUser
    public void testCacheEviction() throws Exception {
        // "store" one key
        when(entityManager.unwrap(any())).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        doNothing().when(session).persist(any());
        PersistentApiKeyType key =
                apiKeyService.create(Role.XROAD_REGISTRATION_OFFICER.name());
        List<PersistentApiKeyType> listOfOne = Arrays.asList(key);
        when(query.list()).thenReturn(listOfOne);
        // then get this key
        apiKeyService.get(key.getPlaintTextKey());
        apiKeyService.get(key.getPlaintTextKey());
        verify(query, times(1)).list();

        // list uses a different cache
        apiKeyService.listAll();
        apiKeyService.listAll();
        verify(query, times(2)).list();

        // create new key to force cache invalidation
        apiKeyService.create(Role.XROAD_REGISTRATION_OFFICER.name());
        apiKeyService.listAll();
        apiKeyService.listAll();
        verify(query, times(3)).list();

        // revoke a key to force cache invalidation
        // (remove(key) itself already uses query.findAll)
        apiKeyService.remove(key.getPlaintTextKey());
        verify(query, times(4)).list();
        apiKeyService.get(key.getPlaintTextKey());
        apiKeyService.get(key.getPlaintTextKey());
        verify(query, times(5)).list();
    }

    @Test
    @WithMockUser
    public void testGet() throws Exception {
        // "store" one key
        when(entityManager.unwrap(any())).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        doNothing().when(session).persist(any());
        PersistentApiKeyType key =
                apiKeyService.create(Role.XROAD_REGISTRATION_OFFICER.name());
        List<PersistentApiKeyType> listOfOne = Arrays.asList(key);
        when(query.list()).thenReturn(listOfOne);
        // then get this key
        apiKeyService.get(key.getPlaintTextKey());
        apiKeyService.get(key.getPlaintTextKey());
        verify(query, times(1)).list();
    }

}
