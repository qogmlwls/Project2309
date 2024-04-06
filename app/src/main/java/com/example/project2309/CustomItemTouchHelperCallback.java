package com.example.project2309;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public
class CustomItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;

    public CustomItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }



    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        Log.i("TAG","getMovementFlags");

        int dragFlags = ItemTouchHelper.START | ItemTouchHelper.END; // 드래그 방향 설정
        return makeMovementFlags(dragFlags, 0); // 여기서는 좌우로 드래그만 허용하고 스와이프는 비활성화합니다.
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {


        Log.i("TAG","onMove");

        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // 스와이프 이벤트 발생 시 동작을 지정할 수 있습니다. 여기서는 사용하지 않습니다.
        Log.i("TAG","onSwiped");

    }

    public interface ItemTouchHelperAdapter {
        void onItemMove(int fromPosition, int toPosition);
    }
}