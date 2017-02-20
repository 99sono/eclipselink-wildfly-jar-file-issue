/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import org.junit.Before;

/**
 *
 * @author b7godin
 */
public class ModelCrudFacadeImplSystemT {

    private static final String WAR_NAME = "generic-logging-interceptor-bug";

    private static int NUMBER_OF_FACADE_CALLS_TO_DO = 500;

    /*
     * ModelCrudFacadeRemote remoteFacade; ModelCrudFacadeRemoteA remoteFacadeA; ModelCrudFacadeRemoteB remoteFacadeB;
     * ModelCrudFacadeRemoteC remoteFacadeC;
     */
    @Before
    public void before() {
        // java:global/generic-logging-interceptor-bug/ModelCrudFacadeImpl!facade.ModelCrudFacadeRemote
        // java:global/generic-logging-interceptor-bug/ModelCrudFacadeImpl!facade.ModelCrudFacadeRemote
        /*
         * remoteFacade = JndiUtil.SINGLETON.resolveBean(WAR_NAME, "ModelCrudFacadeImpl", ModelCrudFacadeRemote.class);
         * remoteFacadeA = JndiUtil.SINGLETON.resolveBean(WAR_NAME, "ModelCrudFacadeAImpl",
         * ModelCrudFacadeRemoteA.class); remoteFacadeB = JndiUtil.SINGLETON.resolveBean(WAR_NAME,
         * "ModelCrudFacadeBImpl", ModelCrudFacadeRemoteB.class); remoteFacadeC =
         * JndiUtil.SINGLETON.resolveBean(WAR_NAME, "ModelCrudFacadeCImpl", ModelCrudFacadeRemoteC.class);
         */

        //
        System.out.println("Deleting all entiteis before test");
        // remoteFacade.deleteAll();
    }

    /*
     * @Test public void tryToGetInterceptorToDie() throws InterruptedException {
     * 
     * ExecutorService executorService = Executors.newFixedThreadPool(20);
     * 
     * System.out.println("Sleep for one second before submiting tasks to the thread pool. ");
     * 
     * try { for (int i = 0; i < NUMBER_OF_FACADE_CALLS_TO_DO; i++) { executorService.execute(new Runnable() {
     * 
     * @Override public void run() { Long result = remoteFacade.countEntities(); System.out.println(
     * "numberOfEntitiesReturned: " + result); } });
     * 
     * executorService.execute(new Runnable() { @Override public void run() { Long result =
     * remoteFacadeA.countEntities(); System.out.println("numberOfEntitiesReturned: " + result); } });
     * 
     * executorService.execute(new Runnable() { @Override public void run() { Long result =
     * remoteFacadeB.countEntities(); System.out.println("numberOfEntitiesReturned: " + result); } });
     * 
     * executorService.execute(new Runnable() {
     * 
     * @Override public void run() { Long result = remoteFacadeC.countEntities(); System.out.println(
     * "numberOfEntitiesReturned: " + result); } }); } } finally { executorService.shutdown(); }
     * 
     * // 2 - wait for all the threads to be finished publishing the POR1 to wm6. while
     * (!executorService.isTerminated()) { System.out.println(
     * "Waiting for executor service to publish all POR1 telegrams to WM6. Sleeping for half a second.");
     * Thread.sleep(500); }
     * 
     * remoteFacade.countEntities(); }
     */
}
