# 동시성 제어 분산락 프로젝트
<br />

## 프로젝트 개요

- Redisson 기반의 분산 락과 Spring AOP를 활용하여 선착순 한정 판매 시스템의 재고 관리를 구현합니다.
- Redis Sorted Set과 Server-Sent Events(SSE)를 이용해 실시간으로 사용자에게 대기열 순번을 제공하는 대기열 시스템을 구성합니다.
- 동시 접속 환경에서 발생할 수 있는 동시성 이슈를 해결함으로써 동시성 제어에 대한 이해를 목적으로 합니다.

<br />

## 프로젝트 환경 및 사용기술

- JAVA, SpringBoot, JPA, Redis, H2, Mysql
- Redisson(Distributed Lock), AOP, Redis Sorted Set, SSE(Server-Sent Events)

<br />

## DistributedLock 분산락

### 시퀀스 다이어그램

![Image](https://github.com/user-attachments/assets/761169bc-ee11-4127-aacd-d51ad4e66318)

- 구매 요청이 들어오면, 분산락 어노테이션을 통해 해당 상품에 대한 락을 시도합니다.
- 락 획득 성공 : 비즈니스 로직 전체가 별도의 트랜잭션으로 실행되며, 재고 수량 검증과 차감이 수행됩니다.
- 재고 차감이 성공적으로 완료되면, 트랜잭션이 커밋되고 이후 락이 해제됩니다.
- 락 획득 실패 : 정해진 횟수만큼 일정 시간 간격으로 락 재시도를 수행합니다.
- 재시도 중에도 락 획득에 계속 실패하거나 재고 부족 등 비즈니스 예외가 발생하면 구매는 실패로 처리됩니다.

<br />

### JMeter 테스트

<img src="https://github.com/user-attachments/assets/091aacb8-6bdf-4605-b48c-7a37bd6cc10e" width="300" height="80"/>

- 한정상품에 대한 수량 100개 설정

### DistributedLock 적용 전

<p align="center">
  <img src="https://github.com/user-attachments/assets/589de809-95bf-4c9f-b6ea-699737fede25" width="340"/>
  <img src="https://github.com/user-attachments/assets/5b3e0627-c135-4085-8af6-24b89b91cc5c" width="340"/>
</p>

- DistributedLock 분산락을 적용하기 전에는 재고수량 100개에 대해 동시에 110개의 쓰레드 요청에 모두 성공처리 되었지만 실제로는 69개의 수량이 남아 중복 처리로 인한 동시성 문제가 발생했습니다.

<br />

### DistributedLock 적용 후

<p align="center">
<img src="https://github.com/user-attachments/assets/b42e5ed0-ec55-4641-b1dc-849113e66cf2" width="340"/>
<img src="https://github.com/user-attachments/assets/b0f88572-35b1-4961-ab9c-dbe512181600" width="340"/>
</p>
- DistributedLock 분산락을 적용한 후에는 재고수량 100개에 대해 110개의 쓰레드를 동시에 요청을 보낸 결과 100개만 락을 획득해 성공하였고 나머지 10개는 수량부족으로 락 획득을 실패하여 처리되지 않았으며 실제 재고 수량도 0개로 정확히 소진되어 동시성제어가 정상적으로 이루어졌습니다.

<br />
<br />

## Redis Sorted Set + SSE(Server-Sent Events) 대기열

### 시퀀스 다이어그램

![Image](https://github.com/user-attachments/assets/0e7bed38-8c40-4531-9483-e557e0dac4ae)

- ZADD: score를 요청시간으로 저장하여 동시접속 10명까지 connected 큐에 추가하고 이후 접속자는  waiting 큐에 추가합니다.
- ZRANGE: score를 기준으로 waiting 큐의 맨 처음 유저를 connected 큐에 추가합니다.
- ZREMOVE : connected 큐에 추가가 되면 waiting 큐에서 삭제합니다.
- ZRANK : Waiting 큐에서 현재 몇번째 순서인지 확인합니다.
- ZCOUNT : 내 뒤에 몇명의 대기중인 유저가 있는지 확인합니다.
- SSE (Server-Sent Events): 실시간 이벤트를 전달하여 현재 나의 Waiting 순위가 몇번째 인지 확인합니다.

### SSE 테스트

<img src="https://github.com/user-attachments/assets/8c510677-e527-4d64-8c45-13e24bd9771f" width="280" height="150"/>
